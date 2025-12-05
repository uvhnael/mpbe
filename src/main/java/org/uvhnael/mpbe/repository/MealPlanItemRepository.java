package org.uvhnael.mpbe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.uvhnael.mpbe.model.MealPlanItem;

import java.util.List;

@Repository
public interface MealPlanItemRepository extends JpaRepository<MealPlanItem, Long> {
    List<MealPlanItem> findByMealPlanId(Long mealPlanId);
    List<MealPlanItem> findByMealPlanIdAndDayOfWeek(Long mealPlanId, Integer dayOfWeek);
    void deleteByMealPlanId(Long mealPlanId);
    void deleteByMealPlanIdAndDayOfWeek(Long mealPlanId, Integer dayOfWeek);
}
