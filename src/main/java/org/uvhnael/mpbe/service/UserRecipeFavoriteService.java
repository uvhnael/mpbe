package org.uvhnael.mpbe.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.uvhnael.mpbe.exception.BadRequestException;
import org.uvhnael.mpbe.exception.ResourceNotFoundException;
import org.uvhnael.mpbe.model.Recipe;
import org.uvhnael.mpbe.model.User;
import org.uvhnael.mpbe.model.UserRecipeFavorite;
import org.uvhnael.mpbe.repository.RecipeRepository;
import org.uvhnael.mpbe.repository.UserRecipeFavoriteRepository;
import org.uvhnael.mpbe.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserRecipeFavoriteService {
    
    private final UserRecipeFavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;
    
    @Transactional
    public UserRecipeFavorite addFavorite(Long userId, Long recipeId) {
        if (favoriteRepository.existsByUserIdAndRecipeId(userId, recipeId)) {
            throw new BadRequestException("Recipe already in favorites");
        }
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Recipe recipe = recipeRepository.findById(recipeId)
            .orElseThrow(() -> new ResourceNotFoundException("Recipe not found"));
        
        UserRecipeFavorite favorite = new UserRecipeFavorite();
        favorite.setUser(user);
        favorite.setRecipe(recipe);
        
        return favoriteRepository.save(favorite);
    }
    
    @Transactional
    public void removeFavorite(Long userId, Long recipeId) {
        if (!favoriteRepository.existsByUserIdAndRecipeId(userId, recipeId)) {
            throw new ResourceNotFoundException("Favorite not found");
        }
        favoriteRepository.deleteByUserIdAndRecipeId(userId, recipeId);
    }
    
    public List<Recipe> getUserFavoriteRecipes(Long userId) {
        return favoriteRepository.findByUserId(userId)
            .stream()
            .map(UserRecipeFavorite::getRecipe)
            .collect(Collectors.toList());
    }
    
    public boolean isFavorite(Long userId, Long recipeId) {
        return favoriteRepository.existsByUserIdAndRecipeId(userId, recipeId);
    }
}
