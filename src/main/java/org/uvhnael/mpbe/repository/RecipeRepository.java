package org.uvhnael.mpbe.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.uvhnael.mpbe.model.Recipe;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    Page<Recipe> findByMealType(String mealType, Pageable pageable);
    Page<Recipe> findByCuisineType(String cuisineType, Pageable pageable);
    List<Recipe> findByCreatedById(Long userId);
    Page<Recipe> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
