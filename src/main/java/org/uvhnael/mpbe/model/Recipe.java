package org.uvhnael.mpbe.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "recipes",
       indexes = {
           @Index(name = "idx_meal_type", columnList = "meal_type"),
           @Index(name = "idx_cuisine_type", columnList = "cuisine_type"),
           @Index(name = "idx_created_by", columnList = "created_by")
       })
@lombok.Getter
@lombok.Setter
@NoArgsConstructor
@AllArgsConstructor
public class Recipe {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "cuisine_type")
    private String cuisineType;
    
    @Column(name = "meal_type")
    private String mealType; // breakfast, lunch, dinner, snack
    
    @Column(name = "prep_time")
    private Integer prepTime;
    
    @Column(name = "cook_time")
    private Integer cookTime;
    
    private Integer servings;
    
    private String difficulty;
    
    @Column(name = "image_url", length = 500)
    private String imageUrl;
    
    @Column(columnDefinition = "TEXT")
    private String instructions;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties({"email","password","fullName","userProfile", "createdAt", "updatedAt", "hibernateLazyInitializer", "handler"})
    private User createdBy;
    
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @com.fasterxml.jackson.annotation.JsonManagedReference
    private List<Ingredient> ingredients;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
