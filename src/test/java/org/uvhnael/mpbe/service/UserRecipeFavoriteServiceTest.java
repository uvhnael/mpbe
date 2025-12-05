package org.uvhnael.mpbe.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.uvhnael.mpbe.exception.ResourceNotFoundException;
import org.uvhnael.mpbe.model.Recipe;
import org.uvhnael.mpbe.model.User;
import org.uvhnael.mpbe.model.UserRecipeFavorite;
import org.uvhnael.mpbe.repository.RecipeRepository;
import org.uvhnael.mpbe.repository.UserRecipeFavoriteRepository;
import org.uvhnael.mpbe.repository.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRecipeFavoriteServiceTest {
    
    @Mock
    private UserRecipeFavoriteRepository favoriteRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private RecipeRepository recipeRepository;
    
    @InjectMocks
    private UserRecipeFavoriteService favoriteService;
    
    private User testUser;
    private Recipe testRecipe;
    
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        
        testRecipe = new Recipe();
        testRecipe.setId(1L);
        testRecipe.setName("Test Recipe");
    }
    
    @Test
    void addFavorite_Success() {
        // Arrange
        when(favoriteRepository.existsByUserIdAndRecipeId(1L, 1L)).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(testRecipe));
        when(favoriteRepository.save(any(UserRecipeFavorite.class))).thenAnswer(i -> i.getArgument(0));
        
        // Act
        UserRecipeFavorite result = favoriteService.addFavorite(1L, 1L);
        
        // Assert
        assertNotNull(result);
        assertEquals(testUser, result.getUser());
        assertEquals(testRecipe, result.getRecipe());
        verify(favoriteRepository).save(any(UserRecipeFavorite.class));
    }
    
    @Test
    void addFavorite_AlreadyExists_ThrowsException() {
        // Arrange
        when(favoriteRepository.existsByUserIdAndRecipeId(1L, 1L)).thenReturn(true);
        
        // Act & Assert
        assertThrows(Exception.class, () -> favoriteService.addFavorite(1L, 1L));
        verify(favoriteRepository, never()).save(any());
    }
    
    @Test
    void removeFavorite_Success() {
        // Arrange
        when(favoriteRepository.existsByUserIdAndRecipeId(1L, 1L)).thenReturn(true);
        doNothing().when(favoriteRepository).deleteByUserIdAndRecipeId(1L, 1L);
        
        // Act
        favoriteService.removeFavorite(1L, 1L);
        
        // Assert
        verify(favoriteRepository).deleteByUserIdAndRecipeId(1L, 1L);
    }
    
    @Test
    void getUserFavoriteRecipes_Success() {
        // Arrange
        UserRecipeFavorite fav1 = new UserRecipeFavorite();
        fav1.setRecipe(testRecipe);
        
        Recipe recipe2 = new Recipe();
        recipe2.setId(2L);
        recipe2.setName("Recipe 2");
        
        UserRecipeFavorite fav2 = new UserRecipeFavorite();
        fav2.setRecipe(recipe2);
        
        when(favoriteRepository.findByUserId(1L)).thenReturn(Arrays.asList(fav1, fav2));
        
        // Act
        List<Recipe> result = favoriteService.getUserFavoriteRecipes(1L);
        
        // Assert
        assertEquals(2, result.size());
        assertEquals("Test Recipe", result.get(0).getName());
        assertEquals("Recipe 2", result.get(1).getName());
    }
    
    @Test
    void isFavorite_ReturnsTrue() {
        // Arrange
        when(favoriteRepository.existsByUserIdAndRecipeId(1L, 1L)).thenReturn(true);
        
        // Act
        boolean result = favoriteService.isFavorite(1L, 1L);
        
        // Assert
        assertTrue(result);
    }
}
