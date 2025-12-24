package org.uvhnael.mpbe.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.uvhnael.mpbe.model.UserProfile;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GeminiAIService {
    
    @Value("${gemini.api.key}")
    private String apiKey;
    
    @Value("${gemini.api.url}")
    private String apiUrl;
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    public String generateMealPlan(UserProfile profile, int days) {
        String prompt = buildMealPlanPrompt(profile, days);
        return callGeminiAPI(prompt);
    }
    
    public String suggestRecipes(UserProfile profile) {
        String prompt = String.format("""
            Suggest 5 recipes based on:
            - Dietary preference: %s
            - Allergies: %s
            - Budget per meal: $%.2f
            
            Return as JSON array with recipe details.
            """,
            profile.getDietaryPreference(),
            profile.getAllergies(),
            profile.getBudgetPerMeal()
        );
        
        return callGeminiAPI(prompt);
    }
    
    public String analyzeNutrition(String recipeName) {
        String prompt = String.format("""
            Analyze the nutritional content of: %s
            
            Return as JSON with calories, protein, carbs, fat, vitamins, and minerals.
            """, recipeName);
        
        return callGeminiAPI(prompt);
    }
    
    private String buildMealPlanPrompt(UserProfile profile, int days) {
        return String.format("""
            Create a %d-day meal plan for:
            - Goal: %s
            - Dietary preference: %s
            - Allergies: %s
            - Budget per meal: $%.2f
            - Activity level: %s
            
            Return as JSON with structure:
            {
              "days": [
                {
                  "day": 1,
                  "meals": [
                    {
                      "type": "breakfast",
                      "name": "Scrambled Eggs with Toast",
                      "description": "A classic breakfast with protein and carbs",
                      "cuisineType": "American",
                      "prepTime": 5,
                      "cookTime": 10,
                      "servings": 1,
                      "difficulty": "easy",
                      "imageUrl": "https://example.com/image.jpg",
                      "ingredients": [
                        {
                          "name": "eggs",
                          "quantity": 2,
                          "unit": "pieces",
                          "calories": 140,
                          "protein": 12,
                          "carbs": 1,
                          "fat": 10
                        },
                        {
                          "name": "bread",
                          "quantity": 2,
                          "unit": "slices",
                          "calories": 160,
                          "protein": 5,
                          "carbs": 30,
                          "fat": 2
                        }
                      ],
                      "instructions": "1. Beat eggs in a bowl\n2. Heat pan and add butter\n3. Cook eggs until done\n4. Toast bread\n5. Serve together",
                      "nutrition": {
                        "calories": 300,
                        "protein": 17,
                        "carbs": 31,
                        "fat": 12
                      }
                    }
                  ]
                }
              ]
            }
            
            IMPORTANT Requirements:
            
            Each meal MUST include:
            - type: meal type (breakfast/lunch/dinner/snack)
            - name: recipe name
            - description: brief description of the dish
            - cuisineType: cuisine origin (e.g., Italian, Mexican, Asian, American)
            - prepTime: preparation time in minutes
            - cookTime: cooking time in minutes
            - servings: number of servings (default 1)
            - difficulty: easy, medium, or hard
            - imageUrl: use placeholder like "https://via.placeholder.com/400x300?text=[MealName]"
            - instructions: step-by-step cooking instructions
            - nutrition: total nutritional values
            
            Each ingredient MUST include:
            - name: ingredient name (string)
            - quantity: numeric amount (number)
            - unit: measurement unit like "g", "ml", "pieces", "cups", "tbsp" (string)
            - calories: calorie content (number)
            - protein: protein in grams (number)
            - carbs: carbohydrates in grams (number)
            - fat: fat in grams (number)
            """,
            days,
            profile.getGoal(),
            profile.getDietaryPreference(),
            profile.getAllergies(),
            profile.getBudgetPerMeal(),
            profile.getActivityLevel()
        );
    }
    
    public String callGeminiAPI(String prompt) {
        try {
            String url = apiUrl + "?key=" + apiKey;
            
            Map<String, Object> request = Map.of(
                "contents", List.of(
                    Map.of("parts", List.of(
                        Map.of("text", prompt)
                    ))
                )
            );
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            
            if (response.getBody() != null) {
                List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.getBody().get("candidates");
                if (candidates != null && !candidates.isEmpty()) {
                    Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
                    List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
                    if (parts != null && !parts.isEmpty()) {
                        return (String) parts.get(0).get("text");
                    }
                }
            }
            
            return "Error: No response from Gemini API";
        } catch (Exception e) {
            return "Error calling Gemini API: " + e.getMessage();
        }
    }
}
