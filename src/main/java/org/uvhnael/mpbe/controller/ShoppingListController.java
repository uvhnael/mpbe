package org.uvhnael.mpbe.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.uvhnael.mpbe.dto.response.ApiResponse;
import org.uvhnael.mpbe.model.MealPlan;
import org.uvhnael.mpbe.model.ShoppingList;
import org.uvhnael.mpbe.service.MealPlanService;
import org.uvhnael.mpbe.repository.ShoppingListRepository;

import java.util.List;

@Tag(name = "Shopping Lists", description = "Shopping list management APIs")
@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping("/shopping-lists")
@RequiredArgsConstructor
public class ShoppingListController {
    
    private final ShoppingListRepository shoppingListRepository;
    private final MealPlanService mealPlanService;
    private final org.uvhnael.mpbe.service.ShoppingListItemService itemService;
    private final org.uvhnael.mpbe.service.ShoppingListGeneratorService generatorService;
    
    @PostMapping
    public ResponseEntity<?> createShoppingList(@RequestParam Long mealPlanId, @RequestParam Long userId) {
        try {
            MealPlan mealPlan = mealPlanService.getMealPlanById(mealPlanId)
                .orElseThrow(() -> new RuntimeException("Meal plan not found"));
            
            ShoppingList shoppingList = new ShoppingList();
            shoppingList.setMealPlan(mealPlan);
            shoppingList.setUser(mealPlan.getUser());
            
            ShoppingList savedList = shoppingListRepository.save(shoppingList);
            
            return ResponseEntity.ok(new ApiResponse(true, "Shopping list created", savedList));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getShoppingListById(@PathVariable Long id) {
        try {
            ShoppingList shoppingList = shoppingListRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shopping list not found"));
            
            return ResponseEntity.ok(new ApiResponse(true, "Success", shoppingList));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage()));
        }
    }
    
    @GetMapping
    public ResponseEntity<?> getUserShoppingLists(@RequestParam Long userId) {
        try {
            List<ShoppingList> shoppingLists = shoppingListRepository.findByUserId(userId);
            return ResponseEntity.ok(new ApiResponse(true, "Success", shoppingLists));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage()));
        }
    }
    
    @PostMapping("/{id}/items")
    public ResponseEntity<?> addItem(
            @PathVariable Long id,
            @RequestBody org.uvhnael.mpbe.model.ShoppingListItem item) {
        try {
            org.uvhnael.mpbe.model.ShoppingListItem savedItem = itemService.addItem(id, item);
            return ResponseEntity.ok(new ApiResponse(true, "Item added", savedItem));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage()));
        }
    }
    
    @GetMapping("/{id}/items")
    public ResponseEntity<?> getItems(@PathVariable Long id) {
        try {
            java.util.List<org.uvhnael.mpbe.model.ShoppingListItem> items = itemService.getItemsByShoppingListId(id);
            return ResponseEntity.ok(new ApiResponse(true, "Success", items));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage()));
        }
    }
    
    @PutMapping("/{id}/items/{itemId}")
    public ResponseEntity<?> updateShoppingListItem(
            @PathVariable Long id,
            @PathVariable Long itemId,
            @RequestBody org.uvhnael.mpbe.model.ShoppingListItem item) {
        try {
            org.uvhnael.mpbe.model.ShoppingListItem updated = itemService.updateItem(itemId, item);
            return ResponseEntity.ok(new ApiResponse(true, "Item updated", updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage()));
        }
    }
    
    @PostMapping("/{id}/generate-from-meal-plan")
    @Operation(summary = "Generate shopping list from meal plan", 
               description = "Automatically generate shopping list items from meal plan recipes")
    public ResponseEntity<?> generateFromMealPlan(@PathVariable Long id) {
        try {
            ShoppingList shoppingList = shoppingListRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shopping list not found"));
            
            if (shoppingList.getMealPlan() == null) {
                return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Shopping list is not linked to a meal plan"));
            }
            
            // Get all recipes from meal plan
            java.util.List<org.uvhnael.mpbe.model.Recipe> recipes = new java.util.ArrayList<>();
            if (shoppingList.getMealPlan().getMealPlanItems() != null) {
                for (org.uvhnael.mpbe.model.MealPlanItem item : shoppingList.getMealPlan().getMealPlanItems()) {
                    if (item.getRecipe() != null) {
                        recipes.add(item.getRecipe());
                    }
                }
            }
            
            // Generate shopping list items
            java.util.List<org.uvhnael.mpbe.model.ShoppingListItem> items = 
                generatorService.generateFromRecipes(shoppingList, recipes);
            
            return ResponseEntity.ok(new ApiResponse(true, 
                "Shopping list generated with " + items.size() + " items", items));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}/items/{itemId}")
    @Operation(summary = "Delete shopping list item", description = "Remove an item from shopping list")
    public ResponseEntity<?> deleteItem(@PathVariable Long id, @PathVariable Long itemId) {
        try {
            itemService.deleteItem(itemId);
            return ResponseEntity.ok(new ApiResponse(true, "Item deleted"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage()));
        }
    }
    
    @PatchMapping("/{id}/items/{itemId}/toggle")
    @Operation(summary = "Toggle item checked status", description = "Mark item as checked or unchecked")
    public ResponseEntity<?> toggleItem(@PathVariable Long id, @PathVariable Long itemId) {
        try {
            itemService.toggleItemChecked(itemId);
            return ResponseEntity.ok(new ApiResponse(true, "Item toggled"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage()));
        }
    }
}
