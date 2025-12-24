package org.uvhnael.mpbe.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "meal_plan_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MealPlanItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "meal_plan_id")
    @com.fasterxml.jackson.annotation.JsonBackReference
    private MealPlan mealPlan;
    
    @ManyToOne
    @JoinColumn(name = "recipe_id")
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties({ "hibernateLazyInitializer", "handler"})
    private Recipe recipe;
    
    @Column(name = "day_of_week")
    private Integer dayOfWeek;
    
    @Column(name = "meal_type")
    private String mealType;
}
