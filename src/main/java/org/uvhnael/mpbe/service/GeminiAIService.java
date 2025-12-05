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
                      "name": "...",
                      "ingredients": [...],
                      "instructions": "...",
                      "nutrition": {...}
                    }
                  ]
                }
              ]
            }
            """,
            days,
            profile.getGoal(),
            profile.getDietaryPreference(),
            profile.getAllergies(),
            profile.getBudgetPerMeal(),
            profile.getActivityLevel()
        );
    }
    
    private String callGeminiAPI(String prompt) {
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
