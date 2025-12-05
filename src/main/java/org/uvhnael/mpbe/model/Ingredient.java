package org.uvhnael.mpbe.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "ingredients")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ingredient {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;
    
    @Column(nullable = false)
    private String name;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal quantity;
    
    private String unit;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal calories;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal protein;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal carbs;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal fat;
}
