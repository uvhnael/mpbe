package org.uvhnael.mpbe.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MealPlanRequest {
    
    @NotNull(message = "Number of days is required")
    @Min(value = 1, message = "Must plan for at least 1 day")
    @Max(value = 30, message = "Cannot plan for more than 30 days")
    private Integer days;
    
    private String startDate;
}
