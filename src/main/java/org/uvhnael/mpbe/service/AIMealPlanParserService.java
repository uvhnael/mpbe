package org.uvhnael.mpbe.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.uvhnael.mpbe.dto.ai.AIMealPlanResponse;
import org.uvhnael.mpbe.model.*;
import org.uvhnael.mpbe.repository.IngredientRepository;
import org.uvhnael.mpbe.repository.MealPlanItemRepository;
import org.uvhnael.mpbe.repository.MealPlanRepository;
import org.uvhnael.mpbe.repository.RecipeRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AIMealPlanParserService {
    
    private final ObjectMapper objectMapper;
    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    private final MealPlanItemRepository mealPlanItemRepository;
    private final MealPlanRepository mealPlanRepository;
    
    @Transactional
    public MealPlan parseAndSaveMealPlan(String aiResponse, MealPlan mealPlan) {
        try {
            // Extract JSON from AI response (handle markdown code blocks)
            String jsonContent = extractJSON(aiResponse);
            
            AIMealPlanResponse aiPlan = objectMapper.readValue(jsonContent, AIMealPlanResponse.class);
            
            List<MealPlanItem> items = new ArrayList<>();
            
            // Save meal plan first to get ID
            MealPlan savedMealPlan = mealPlanRepository.save(mealPlan);
            
            for (AIMealPlanResponse.AIDayPlan day : aiPlan.getDays()) {
                for (AIMealPlanResponse.AIMeal meal : day.getMeals()) {
                    // Create or find recipe
                    Recipe recipe = createRecipeFromAIMeal(meal, savedMealPlan.getUser());
                    
                    // Create meal plan item
                    MealPlanItem item = new MealPlanItem();
                    item.setMealPlan(savedMealPlan);
                    item.setRecipe(recipe);
                    item.setDayOfWeek(day.getDay());
                    item.setMealType(meal.getType());
                    
                    items.add(item);
                }
            }
            
            // Save all meal plan items
            mealPlanItemRepository.saveAll(items);
            savedMealPlan.setMealPlanItems(items);
            
            return savedMealPlan;
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse AI response: " + e.getMessage(), e);
        }
    }
    
    private String extractJSON(String aiResponse) {
        String response = aiResponse.trim();
        
        // Find JSON block within markdown code fences
        int jsonStart = response.indexOf("```json");
        if (jsonStart != -1) {
            // Found ```json marker
            int jsonContentStart = response.indexOf('\n', jsonStart) + 1;
            int jsonEnd = response.indexOf("```", jsonContentStart);
            if (jsonEnd != -1) {
                return response.substring(jsonContentStart, jsonEnd).trim();
            }
        }
        
        // Try finding generic ``` markers
        jsonStart = response.indexOf("```");
        if (jsonStart != -1) {
            int jsonContentStart = response.indexOf('\n', jsonStart) + 1;
            int jsonEnd = response.indexOf("```", jsonContentStart);
            if (jsonEnd != -1) {
                return response.substring(jsonContentStart, jsonEnd).trim();
            }
        }
        
        // No markdown found, try to find JSON by looking for opening brace
        jsonStart = response.indexOf('{');
        if (jsonStart != -1) {
            return response.substring(jsonStart).trim();
        }
        
        // Return as-is if nothing worked
        return response;
    }
    
    private Recipe createRecipeFromAIMeal(AIMealPlanResponse.AIMeal meal, User user) {
        Recipe recipe = new Recipe();
        recipe.setName(meal.getName());
        recipe.setInstructions(meal.getInstructions());
        recipe.setMealType(meal.getType());
        recipe.setPrepTime(meal.getPrepTime());
        recipe.setCookTime(meal.getCookTime());
        recipe.setCreatedBy(user);
        
        // Set description from nutrition info
        if (meal.getNutrition() != null) {
            recipe.setDescription(String.format("Calories: %s, Protein: %s, Carbs: %s, Fat: %s",
                meal.getNutrition().getCalories() != null ? meal.getNutrition().getCalories() : "N/A",
                meal.getNutrition().getProtein() != null ? meal.getNutrition().getProtein() : "N/A",
                meal.getNutrition().getCarbs() != null ? meal.getNutrition().getCarbs() : "N/A",
                meal.getNutrition().getFat() != null ? meal.getNutrition().getFat() : "N/A"
            ));
        }
        
        recipe = recipeRepository.save(recipe);
        
        // Create ingredients
        if (meal.getIngredients() != null) {
            List<Ingredient> ingredients = new ArrayList<>();
            for (String ingredientStr : meal.getIngredients()) {
                Ingredient ingredient = new Ingredient();
                ingredient.setRecipe(recipe);
                ingredient.setName(ingredientStr);
                ingredient.setQuantity(null); // Parse from string if needed
                ingredient.setUnit(null);
                
                ingredients.add(ingredient);
            }
            ingredientRepository.saveAll(ingredients);
            recipe.setIngredients(ingredients);
        }
        
        return recipe;
    }
}
