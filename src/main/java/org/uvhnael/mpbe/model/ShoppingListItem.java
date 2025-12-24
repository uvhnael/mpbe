package org.uvhnael.mpbe.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "shopping_list_items",
       indexes = {
           @Index(name = "idx_shopping_list_id", columnList = "shopping_list_id")
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingListItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shopping_list_id", nullable = false)
    @JsonBackReference
    private ShoppingList shoppingList;
    
    @Column(name = "ingredient_name", nullable = false)
    private String ingredientName;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal quantity;
    
    @Column(nullable = false)
    private String unit;
    
    @Column(name = "is_checked", nullable = false)
    private Boolean isChecked = false;
    
    private String category; // dairy, meat, vegetables, etc.
}
