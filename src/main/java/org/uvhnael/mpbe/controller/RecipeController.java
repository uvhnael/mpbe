package org.uvhnael.mpbe.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.uvhnael.mpbe.dto.response.ApiResponse;
import org.uvhnael.mpbe.model.Recipe;
import org.uvhnael.mpbe.service.RecipeService;

@Tag(name = "Recipes", description = "Recipe management APIs")
@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping("/recipes")
@RequiredArgsConstructor
public class RecipeController {
    
    private final RecipeService recipeService;
    private final org.uvhnael.mpbe.service.UserRecipeFavoriteService favoriteService;
    private final org.uvhnael.mpbe.service.NutritionService nutritionService;
    
    @GetMapping
    @Operation(summary = "Get all recipes", description = "Get paginated list of all recipes")
    public ResponseEntity<?> getAllRecipes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Recipe> recipes = recipeService.getAllRecipes(pageable);
            org.uvhnael.mpbe.dto.response.PagedResponse<Recipe> pagedResponse = 
                org.uvhnael.mpbe.dto.response.PagedResponse.of(recipes);
            
            return ResponseEntity.ok(new ApiResponse(true, "Success", pagedResponse));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getRecipeById(@PathVariable Long id) {
        try {
            Recipe recipe = recipeService.getRecipeById(id)
                .orElseThrow(() -> new RuntimeException("Recipe not found"));
            
            return ResponseEntity.ok(new ApiResponse(true, "Success", recipe));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage()));
        }
    }
    
    @PostMapping
    public ResponseEntity<?> createRecipe(@RequestBody Recipe recipe) {
        try {
            Recipe savedRecipe = recipeService.createRecipe(recipe);
            return ResponseEntity.ok(new ApiResponse(true, "Recipe created", savedRecipe));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateRecipe(@PathVariable Long id, @RequestBody Recipe recipe) {
        try {
            Recipe updatedRecipe = recipeService.updateRecipe(id, recipe);
            return ResponseEntity.ok(new ApiResponse(true, "Recipe updated", updatedRecipe));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRecipe(@PathVariable Long id) {
        try {
            recipeService.deleteRecipe(id);
            return ResponseEntity.ok(new ApiResponse(true, "Recipe deleted"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage()));
        }
    }
    
    @GetMapping("/search")
    public ResponseEntity<?> searchRecipes(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Recipe> recipes = recipeService.searchRecipes(name, pageable);
            
            return ResponseEntity.ok(new ApiResponse(true, "Success", recipes));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage()));
        }
    }
    
    @PostMapping("/{id}/favorite")
    public ResponseEntity<?> favoriteRecipe(
            @PathVariable Long id,
            @RequestParam Long userId) {
        try {
            favoriteService.addFavorite(userId, id);
            return ResponseEntity.ok(new ApiResponse(true, "Recipe added to favorites"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}/favorite")
    public ResponseEntity<?> unfavoriteRecipe(
            @PathVariable Long id,
            @RequestParam Long userId) {
        try {
            favoriteService.removeFavorite(userId, id);
            return ResponseEntity.ok(new ApiResponse(true, "Recipe removed from favorites"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage()));
        }
    }
    
    @GetMapping("/favorites")
    public ResponseEntity<?> getUserFavorites(@RequestParam Long userId) {
        try {
            java.util.List<Recipe> favorites = favoriteService.getUserFavoriteRecipes(userId);
            return ResponseEntity.ok(new ApiResponse(true, "Success", favorites));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage()));
        }
    }
    
    @GetMapping("/{id}/nutrition")
    @Operation(summary = "Get nutrition information", description = "Calculate nutritional information for a recipe")
    public ResponseEntity<?> getRecipeNutrition(@PathVariable Long id) {
        try {
            Recipe recipe = recipeService.getRecipeById(id)
                .orElseThrow(() -> new RuntimeException("Recipe not found"));
            
            java.util.Map<String, Object> nutrition = nutritionService.calculateNutrition(recipe);
            return ResponseEntity.ok(new ApiResponse(true, "Success", nutrition));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage()));
        }
    }
}
