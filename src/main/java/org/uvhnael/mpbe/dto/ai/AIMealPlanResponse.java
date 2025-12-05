package org.uvhnael.mpbe.dto.ai;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AIMealPlanResponse {
    private List<AIDayPlan> days;
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AIDayPlan {
        private Integer day;
        private List<AIMeal> meals;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AIMeal {
        private String type; // breakfast, lunch, dinner, snack
        private String name;
        private List<String> ingredients; // Changed to accept simple string array
        private String instructions;
        private AINutrition nutrition;
        private Integer prepTime;
        private Integer cookTime;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AINutrition {
        private String calories;  // Accept "~200 kcal" format
        private String protein;   // Accept "~6 g" format
        
        @com.fasterxml.jackson.annotation.JsonAlias({"carbohydrates", "carbs"})
        private String carbs;     // Accept both "carbs" and "carbohydrates"
        
        @com.fasterxml.jackson.annotation.JsonAlias({"fats", "fat"})
        private String fat;       // Accept both "fat" and "fats"
        
        private String fiber;
    }
}
