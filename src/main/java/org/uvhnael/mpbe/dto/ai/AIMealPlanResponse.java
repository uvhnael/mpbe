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
        private String description;
        private String cuisineType;
        private Integer prepTime;
        private Integer cookTime;
        private Integer servings;
        private String difficulty;
        private String imageUrl;
        private List<AIIngredient> ingredients; // Support full ingredient objects
        private String instructions;
        private AINutrition nutrition;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AIIngredient {
        private String name;
        private java.math.BigDecimal quantity;
        private String unit;
        private java.math.BigDecimal calories;
        private java.math.BigDecimal protein;
        private java.math.BigDecimal carbs;
        private java.math.BigDecimal fat;
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
