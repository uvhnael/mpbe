package org.uvhnael.mpbe.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.uvhnael.mpbe.exception.ResourceNotFoundException;
import org.uvhnael.mpbe.model.Ingredient;
import org.uvhnael.mpbe.model.Recipe;
import org.uvhnael.mpbe.model.ShoppingList;
import org.uvhnael.mpbe.model.ShoppingListItem;
import org.uvhnael.mpbe.repository.ShoppingListItemRepository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShoppingListGeneratorService {
    
    private final ShoppingListItemRepository itemRepository;
    
    /**
     * Generate shopping list items from meal plan recipes
     */
    @Transactional
    public List<ShoppingListItem> generateFromRecipes(ShoppingList shoppingList, List<Recipe> recipes) {
        log.info("Generating shopping list items for shopping list {}, {} recipes", 
                 shoppingList.getId(), recipes.size());
        
        // Group ingredients by name and sum quantities
        Map<String, IngredientAggregate> aggregated = new HashMap<>();
        
        for (Recipe recipe : recipes) {
            if (recipe.getIngredients() != null) {
                for (Ingredient ingredient : recipe.getIngredients()) {
                    String key = ingredient.getName().toLowerCase();
                    
                    if (aggregated.containsKey(key)) {
                        IngredientAggregate agg = aggregated.get(key);
                        // Only sum if units match
                        if (agg.unit.equalsIgnoreCase(ingredient.getUnit())) {
                            agg.quantity = agg.quantity.add(ingredient.getQuantity());
                        } else {
                            // Different units - create separate entry
                            key = key + "_" + ingredient.getUnit();
                            aggregated.put(key, new IngredientAggregate(
                                ingredient.getName(),
                                ingredient.getQuantity(),
                                ingredient.getUnit()
                            ));
                        }
                    } else {
                        aggregated.put(key, new IngredientAggregate(
                            ingredient.getName(),
                            ingredient.getQuantity(),
                            ingredient.getUnit()
                        ));
                    }
                }
            }
        }
        
        // Create shopping list items
        List<ShoppingListItem> items = new java.util.ArrayList<>();
        for (IngredientAggregate agg : aggregated.values()) {
            ShoppingListItem item = new ShoppingListItem();
            item.setShoppingList(shoppingList);
            item.setIngredientName(agg.name);
            item.setQuantity(agg.quantity);
            item.setUnit(agg.unit);
            item.setIsChecked(false);
            item.setCategory(categorizeIngredient(agg.name));
            
            items.add(item);
        }
        
        List<ShoppingListItem> saved = itemRepository.saveAll(items);
        log.info("Generated {} shopping list items", saved.size());
        
        return saved;
    }
    
    /**
     * Categorize ingredient for better organization
     */
    private String categorizeIngredient(String name) {
        String lowerName = name.toLowerCase();
        
        if (lowerName.matches(".*(chicken|beef|pork|fish|meat|lamb).*")) {
            return "Meat & Seafood";
        } else if (lowerName.matches(".*(milk|cheese|yogurt|butter|cream).*")) {
            return "Dairy";
        } else if (lowerName.matches(".*(carrot|potato|onion|tomato|lettuce|spinach|broccoli).*")) {
            return "Vegetables";
        } else if (lowerName.matches(".*(apple|banana|orange|berry|grape|lemon).*")) {
            return "Fruits";
        } else if (lowerName.matches(".*(bread|pasta|rice|flour|cereal).*")) {
            return "Grains & Bakery";
        } else if (lowerName.matches(".*(salt|pepper|sugar|spice|herb|sauce).*")) {
            return "Condiments & Spices";
        } else {
            return "Other";
        }
    }
    
    /**
     * Clear all items from shopping list
     */
    @Transactional
    public void clearShoppingList(Long shoppingListId) {
        log.info("Clearing shopping list: {}", shoppingListId);
        itemRepository.deleteByShoppingListId(shoppingListId);
    }
    
    /**
     * Inner class to aggregate ingredients
     */
    private static class IngredientAggregate {
        String name;
        BigDecimal quantity;
        String unit;
        
        IngredientAggregate(String name, BigDecimal quantity, String unit) {
            this.name = name;
            this.quantity = quantity;
            this.unit = unit;
        }
    }
}
