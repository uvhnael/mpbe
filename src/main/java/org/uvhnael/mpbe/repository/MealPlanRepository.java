package org.uvhnael.mpbe.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.uvhnael.mpbe.model.MealPlan;

import java.util.List;

@Repository
public interface MealPlanRepository extends JpaRepository<MealPlan, Long> {
    List<MealPlan> findByUserId(Long userId);
    Page<MealPlan> findByUserId(Long userId, Pageable pageable);
    List<MealPlan> findByUserIdAndStatus(Long userId, String status);
}
