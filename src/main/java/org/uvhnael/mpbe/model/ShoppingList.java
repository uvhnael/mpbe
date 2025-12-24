package org.uvhnael.mpbe.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "shopping_lists",
       indexes = {
           @Index(name = "idx_user_id", columnList = "user_id"),
           @Index(name = "idx_meal_plan_id", columnList = "meal_plan_id")
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingList {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meal_plan_id")
    @JsonIgnoreProperties({"user", "mealPlanItems", "hibernateLazyInitializer", "handler"})
    private MealPlan mealPlan;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties({"password","fullName","userProfile", "createdAt", "updatedAt", "hibernateLazyInitializer", "handler"})
    private User user;
    
    @OneToMany(mappedBy = "shoppingList", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<ShoppingListItem> items;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
