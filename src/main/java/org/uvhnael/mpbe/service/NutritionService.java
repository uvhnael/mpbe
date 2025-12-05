package org.uvhnael.mpbe.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.uvhnael.mpbe.model.Recipe;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NutritionService {
    
    /**
     * Calculate basic nutrition information for a recipe
     * This is a simplified calculation - in production, you'd integrate with a nutrition database API
     */
    public Map<String, Object> calculateNutrition(Recipe recipe) {
        log.info("Calculating nutrition for recipe: {}", recipe.getName());
        
        Map<String, Object> nutrition = new HashMap<>();
        
        try {
            // Simplified calculation based on ingredient count and servings
            // In production, integrate with USDA FoodData Central or similar API
            int ingredientCount = recipe.getIngredients() != null ? recipe.getIngredients().size() : 0;
            int servings = recipe.getServings() != null ? recipe.getServings() : 1;
            
            // Rough estimates (should be replaced with actual data)
            double estimatedCalories = ingredientCount * 150.0 / servings;
            double estimatedProtein = ingredientCount * 15.0 / servings;
            double estimatedCarbs = ingredientCount * 20.0 / servings;
            double estimatedFat = ingredientCount * 8.0 / servings;
            
            nutrition.put("calories", round(estimatedCalories, 0));
            nutrition.put("protein", round(estimatedProtein, 1) + "g");
            nutrition.put("carbohydrates", round(estimatedCarbs, 1) + "g");
            nutrition.put("fat", round(estimatedFat, 1) + "g");
            nutrition.put("servings", servings);
            nutrition.put("caloriesPerServing", round(estimatedCalories, 0));
            
            log.debug("Nutrition calculated: {}", nutrition);
            
        } catch (Exception e) {
            log.error("Error calculating nutrition for recipe {}: {}", recipe.getId(), e.getMessage(), e);
            nutrition.put("error", "Unable to calculate nutrition information");
        }
        
        return nutrition;
    }
    
    /**
     * Calculate daily nutrition totals for a meal plan
     */
    public Map<String, Object> calculateDailyTotals(java.util.List<Recipe> recipes) {
        log.info("Calculating daily nutrition totals for {} recipes", recipes.size());
        
        Map<String, Object> totals = new HashMap<>();
        
        double totalCalories = 0;
        double totalProtein = 0;
        double totalCarbs = 0;
        double totalFat = 0;
        
        for (Recipe recipe : recipes) {
            Map<String, Object> recipeNutrition = calculateNutrition(recipe);
            
            if (recipeNutrition.containsKey("calories")) {
                totalCalories += (double) recipeNutrition.get("calories");
            }
            // Parse protein, carbs, fat (removing 'g' suffix)
            if (recipeNutrition.containsKey("protein")) {
                String proteinStr = recipeNutrition.get("protein").toString().replace("g", "");
                totalProtein += Double.parseDouble(proteinStr);
            }
            if (recipeNutrition.containsKey("carbohydrates")) {
                String carbsStr = recipeNutrition.get("carbohydrates").toString().replace("g", "");
                totalCarbs += Double.parseDouble(carbsStr);
            }
            if (recipeNutrition.containsKey("fat")) {
                String fatStr = recipeNutrition.get("fat").toString().replace("g", "");
                totalFat += Double.parseDouble(fatStr);
            }
        }
        
        totals.put("totalCalories", round(totalCalories, 0));
        totals.put("totalProtein", round(totalProtein, 1) + "g");
        totals.put("totalCarbohydrates", round(totalCarbs, 1) + "g");
        totals.put("totalFat", round(totalFat, 1) + "g");
        
        log.debug("Daily totals: {}", totals);
        
        return totals;
    }
    
    /**
     * Validate if recipe meets dietary restrictions
     */
    public boolean meetsDietaryRestrictions(Recipe recipe, String dietaryPreference, String allergies) {
        log.info("Checking dietary restrictions for recipe: {}, preference: {}, allergies: {}", 
                 recipe.getName(), dietaryPreference, allergies);
        
        // Simplified check - in production, check ingredients against restrictions
        if (recipe.getDescription() != null && allergies != null) {
            String lowerDesc = recipe.getDescription().toLowerCase();
            String[] allergyList = allergies.toLowerCase().split(",");
            
            for (String allergy : allergyList) {
                if (lowerDesc.contains(allergy.trim())) {
                    log.warn("Recipe {} contains allergen: {}", recipe.getName(), allergy);
                    return false;
                }
            }
        }
        
        return true;
    }
    
    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
