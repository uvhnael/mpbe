package org.uvhnael.mpbe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.uvhnael.mpbe.model.UserRecipeFavorite;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRecipeFavoriteRepository extends JpaRepository<UserRecipeFavorite, Long> {
    List<UserRecipeFavorite> findByUserId(Long userId);
    Optional<UserRecipeFavorite> findByUserIdAndRecipeId(Long userId, Long recipeId);
    boolean existsByUserIdAndRecipeId(Long userId, Long recipeId);
    void deleteByUserIdAndRecipeId(Long userId, Long recipeId);
}
