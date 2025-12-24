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
    private final GeminiAIService geminiAIService;
    
    /**
     * Generate shopping list items from meal plan recipes using AI
     */
    @Transactional
    public List<ShoppingListItem> generateFromRecipes(ShoppingList shoppingList, List<Recipe> recipes) {
        log.info("Generating shopping list items for shopping list {}, {} recipes", 
                 shoppingList.getId(), recipes.size());
        
        try {
            // Build prompt with all ingredients
            StringBuilder ingredientsPrompt = new StringBuilder();
            ingredientsPrompt.append("Optimize the following ingredients for a shopping list:\n\n");
            
            for (Recipe recipe : recipes) {
                ingredientsPrompt.append("Recipe: ").append(recipe.getName()).append("\n");
                if (recipe.getIngredients() != null) {
                    for (Ingredient ingredient : recipe.getIngredients()) {
                        ingredientsPrompt.append("- ")
                            .append(ingredient.getQuantity() != null ? ingredient.getQuantity() : "")
                            .append(" ")
                            .append(ingredient.getUnit() != null ? ingredient.getUnit() : "")
                            .append(" ")
                            .append(ingredient.getName())
                            .append("\n");
                    }
                }
                ingredientsPrompt.append("\n");
            }
            
            ingredientsPrompt.append("""
                
                Please:
                1. Combine duplicate ingredients with the same unit
                2. Convert units to standard measurements where appropriate
                3. Categorize items (Meat & Seafood, Dairy, Vegetables, Fruits, Grains & Bakery, Condiments & Spices, Other)
                4. Suggest practical shopping quantities (round up to store packages)
                
                Return as JSON array:
                [
                  {
                    "name": "ingredient name",
                    "quantity": 2.5,
                    "unit": "kg",
                    "category": "Vegetables",
                    "note": "optional shopping tip"
                  }
                ]
                """);
            
            String aiResponse = geminiAIService.callGeminiAPI(ingredientsPrompt.toString());
            
            // Try to parse AI response
            List<ShoppingListItem> items = parseAIShoppingList(shoppingList, aiResponse);
            
            if (items.isEmpty()) {
                log.warn("AI parsing failed, falling back to basic aggregation");
                items = generateBasicShoppingList(shoppingList, recipes);
            }
            
            List<ShoppingListItem> saved = itemRepository.saveAll(items);
            log.info("Generated {} shopping list items using AI", saved.size());
            
            return saved;
            
        } catch (Exception e) {
            log.error("Error generating shopping list with AI, falling back to basic method", e);
            return generateBasicShoppingList(shoppingList, recipes);
        }
    }
    
    /**
     * Fallback method - basic ingredient aggregation
     */
    private List<ShoppingListItem> generateBasicShoppingList(ShoppingList shoppingList, List<Recipe> recipes) {
        Map<String, IngredientAggregate> aggregated = new HashMap<>();
        
        for (Recipe recipe : recipes) {
            if (recipe.getIngredients() != null) {
                for (Ingredient ingredient : recipe.getIngredients()) {
                    String key = ingredient.getName().toLowerCase();
                    String unit = ingredient.getUnit() != null && !ingredient.getUnit().isEmpty() 
                        ? ingredient.getUnit() : "unit";
                    BigDecimal quantity = ingredient.getQuantity() != null ? ingredient.getQuantity() : BigDecimal.ZERO;
                    
                    if (aggregated.containsKey(key)) {
                        IngredientAggregate agg = aggregated.get(key);
                        if (agg.unit.equalsIgnoreCase(unit)) {
                            agg.quantity = agg.quantity.add(quantity);
                        } else {
                            key = key + "_" + unit;
                            aggregated.put(key, new IngredientAggregate(
                                ingredient.getName(),
                                quantity,
                                unit
                            ));
                        }
                    } else {
                        aggregated.put(key, new IngredientAggregate(
                            ingredient.getName(),
                            quantity,
                            unit
                        ));
                    }
                }
            }
        }
        
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
        
        return items;
    }
    
    /**
     * Parse AI response to create shopping list items
     */
    private List<ShoppingListItem> parseAIShoppingList(ShoppingList shoppingList, String aiResponse) {
        List<ShoppingListItem> items = new java.util.ArrayList<>();
        
        try {
            // Check if response is an error message
            if (aiResponse.startsWith("Error")) {
                log.warn("AI returned error: {}", aiResponse);
                return items; // Return empty list to trigger fallback
            }
            
            // Extract JSON from response
            String json = extractJSON(aiResponse);
            
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.databind.JsonNode root = mapper.readTree(json);
            
            if (root.isArray()) {
                for (com.fasterxml.jackson.databind.JsonNode node : root) {
                    ShoppingListItem item = new ShoppingListItem();
                    item.setShoppingList(shoppingList);
                    item.setIngredientName(node.get("name").asText());
                    item.setQuantity(new BigDecimal(node.get("quantity").asText()));
                    item.setUnit(node.get("unit").asText());
                    item.setCategory(node.has("category") ? node.get("category").asText() : "Other");
                    item.setIsChecked(false);
                    
                    items.add(item);
                }
            }
        } catch (Exception e) {
            log.warn("Failed to parse AI response, will use fallback method: {}", e.getMessage());
        }
        
        return items;
    }
    
    /**
     * Extract JSON from AI response
     */
    private String extractJSON(String response) {
        if (response.contains("```json")) {
            int start = response.indexOf("```json") + 7;
            int end = response.indexOf("```", start);
            return response.substring(start, end).trim();
        } else if (response.contains("[")) {
            return response.substring(response.indexOf("[")).trim();
        }
        return response;
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
