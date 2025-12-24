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
            
            // Sanitize JSON - escape unescaped newlines in string values
            jsonContent = sanitizeJSON(jsonContent);
            
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
            
            // Calculate total calories from all meals
            int totalCalories = 0;
            for (AIMealPlanResponse.AIDayPlan day : aiPlan.getDays()) {
                for (AIMealPlanResponse.AIMeal meal : day.getMeals()) {
                    if (meal.getNutrition() != null && meal.getNutrition().getCalories() != null) {
                        try {
                            // Extract numeric value from string like "300" or "~300 kcal"
                            String caloriesStr = meal.getNutrition().getCalories().replaceAll("[^0-9]", "");
                            if (!caloriesStr.isEmpty()) {
                                totalCalories += Integer.parseInt(caloriesStr);
                            }
                        } catch (Exception e) {
                            // Skip if cannot parse
                        }
                    }
                }
            }
            
            savedMealPlan.setTotalCalories(totalCalories);
            mealPlanRepository.save(savedMealPlan);
            
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
    
    /**
     * Sanitize JSON by escaping unescaped newlines and other control characters in string values
     */
    private String sanitizeJSON(String json) {
        StringBuilder result = new StringBuilder();
        boolean inString = false;
        boolean escaped = false;
        
        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            
            if (escaped) {
                result.append(c);
                escaped = false;
                continue;
            }
            
            if (c == '\\') {
                result.append(c);
                escaped = true;
                continue;
            }
            
            if (c == '"') {
                result.append(c);
                inString = !inString;
                continue;
            }
            
            // If we're inside a string and encounter control characters, escape them
            if (inString) {
                if (c == '\n') {
                    result.append("\\n");
                } else if (c == '\r') {
                    result.append("\\r");
                } else if (c == '\t') {
                    result.append("\\t");
                } else {
                    result.append(c);
                }
            } else {
                result.append(c);
            }
        }
        
        return result.toString();
    }
    
    private Recipe createRecipeFromAIMeal(AIMealPlanResponse.AIMeal meal, User user) {
        Recipe recipe = new Recipe();
        recipe.setName(meal.getName());
        recipe.setDescription(meal.getDescription());
        recipe.setCuisineType(meal.getCuisineType());
        recipe.setMealType(meal.getType());
        recipe.setPrepTime(meal.getPrepTime());
        recipe.setCookTime(meal.getCookTime());
        recipe.setServings(meal.getServings() != null ? meal.getServings() : 1);
        recipe.setDifficulty(meal.getDifficulty());
        recipe.setImageUrl(meal.getImageUrl());
        recipe.setInstructions(meal.getInstructions());
        recipe.setCreatedBy(user);
        
        recipe = recipeRepository.save(recipe);
        
        // Create ingredients
        if (meal.getIngredients() != null) {
            List<Ingredient> ingredients = new ArrayList<>();
            for (AIMealPlanResponse.AIIngredient aiIngredient : meal.getIngredients()) {
                Ingredient ingredient = new Ingredient();
                ingredient.setRecipe(recipe);
                ingredient.setName(aiIngredient.getName());
                ingredient.setQuantity(aiIngredient.getQuantity());
                ingredient.setUnit(aiIngredient.getUnit() != null ? aiIngredient.getUnit() : "unit");
                ingredient.setCalories(aiIngredient.getCalories());
                ingredient.setProtein(aiIngredient.getProtein());
                ingredient.setCarbs(aiIngredient.getCarbs());
                ingredient.setFat(aiIngredient.getFat());
                
                ingredients.add(ingredient);
            }
            ingredientRepository.saveAll(ingredients);
            recipe.setIngredients(ingredients);
        }
        
        return recipe;
    }
}
