package org.uvhnael.mpbe.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    private Long id;
    private Long userId;
    private Integer age;
    private String gender;
    private BigDecimal height;
    private BigDecimal weight;
    private String goal;
    private String activityLevel;
    private String dietaryPreference;
    private String allergies;
    private BigDecimal budgetPerMeal;
}
