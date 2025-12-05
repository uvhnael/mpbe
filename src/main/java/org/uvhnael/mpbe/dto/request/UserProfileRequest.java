package org.uvhnael.mpbe.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserProfileRequest {
    
    @Min(value = 10, message = "Age must be at least 10")
    @Max(value = 120, message = "Age must not exceed 120")
    private Integer age;
    
    private String gender;
    
    @DecimalMin(value = "50.0", message = "Height must be at least 50 cm")
    private BigDecimal height;
    
    @DecimalMin(value = "20.0", message = "Weight must be at least 20 kg")
    private BigDecimal weight;
    
    private String goal;
    private String activityLevel;
    private String dietaryPreference;
    private String allergies;
    
    @DecimalMin(value = "0.0", message = "Budget must be non-negative")
    private BigDecimal budgetPerMeal;
}
