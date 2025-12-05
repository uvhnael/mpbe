package org.uvhnael.mpbe.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.uvhnael.mpbe.model.Recipe;
import org.uvhnael.mpbe.repository.RecipeRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecipeService {
    
    private final RecipeRepository recipeRepository;
    
    public Page<Recipe> getAllRecipes(Pageable pageable) {
        return recipeRepository.findAll(pageable);
    }
    
    public Optional<Recipe> getRecipeById(Long id) {
        return recipeRepository.findById(id);
    }
    
    public Recipe createRecipe(Recipe recipe) {
        return recipeRepository.save(recipe);
    }
    
    public Recipe updateRecipe(Long id, Recipe recipe) {
        Recipe existingRecipe = recipeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recipe not found"));
        
        existingRecipe.setName(recipe.getName());
        existingRecipe.setDescription(recipe.getDescription());
        existingRecipe.setCuisineType(recipe.getCuisineType());
        existingRecipe.setMealType(recipe.getMealType());
        existingRecipe.setPrepTime(recipe.getPrepTime());
        existingRecipe.setCookTime(recipe.getCookTime());
        existingRecipe.setServings(recipe.getServings());
        existingRecipe.setDifficulty(recipe.getDifficulty());
        existingRecipe.setImageUrl(recipe.getImageUrl());
        existingRecipe.setInstructions(recipe.getInstructions());
        
        return recipeRepository.save(existingRecipe);
    }
    
    public void deleteRecipe(Long id) {
        recipeRepository.deleteById(id);
    }
    
    public Page<Recipe> searchRecipes(String name, Pageable pageable) {
        return recipeRepository.findByNameContainingIgnoreCase(name, pageable);
    }
    
    public Page<Recipe> getRecipesByMealType(String mealType, Pageable pageable) {
        return recipeRepository.findByMealType(mealType, pageable);
    }
    
    public List<Recipe> getUserRecipes(Long userId) {
        return recipeRepository.findByCreatedById(userId);
    }
}
