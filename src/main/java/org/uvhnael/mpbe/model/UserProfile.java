package org.uvhnael.mpbe.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "user_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    @com.fasterxml.jackson.annotation.JsonBackReference
    private User user;
    
    private Integer age;
    
    private String gender;
    
    @Column(precision = 5, scale = 2)
    private BigDecimal height;
    
    @Column(precision = 5, scale = 2)
    private BigDecimal weight;
    
    private String goal; // weight_loss, muscle_gain, maintain
    
    @Column(name = "activity_level")
    private String activityLevel;
    
    @Column(name = "dietary_preference")
    private String dietaryPreference; // vegetarian, vegan, keto, etc.
    
    @Column(columnDefinition = "TEXT")
    private String allergies;
    
    @Column(name = "budget_per_meal", precision = 10, scale = 2)
    private BigDecimal budgetPerMeal;
}
