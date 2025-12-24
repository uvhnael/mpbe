package org.uvhnael.mpbe.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.uvhnael.mpbe.model.MealPlan;
import org.uvhnael.mpbe.repository.MealPlanRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MealPlanService {
    
    private final MealPlanRepository mealPlanRepository;
    
    public List<MealPlan> getUserMealPlans(Long userId) {
        return mealPlanRepository.findByUserId(userId);
    }
    
    public Page<MealPlan> getUserMealPlans(Long userId, Pageable pageable) {
        return mealPlanRepository.findByUserId(userId, pageable);
    }
    
    public Optional<MealPlan> getMealPlanById(Long id) {
        return mealPlanRepository.findById(id);
    }
    
    public MealPlan createMealPlan(MealPlan mealPlan) {
        return mealPlanRepository.save(mealPlan);
    }
    
    public MealPlan updateMealPlan(Long id, MealPlan mealPlan) {
        MealPlan existingPlan = mealPlanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Meal plan not found"));
        
        if (mealPlan.getName() != null) {
            existingPlan.setName(mealPlan.getName());
        }
        existingPlan.setStartDate(mealPlan.getStartDate());
        existingPlan.setEndDate(mealPlan.getEndDate());
        existingPlan.setTotalCalories(mealPlan.getTotalCalories());
        existingPlan.setStatus(mealPlan.getStatus());
        
        return mealPlanRepository.save(existingPlan);
    }
    
    public MealPlan updateMealPlanName(Long id, String name) {
        MealPlan existingPlan = mealPlanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Meal plan not found"));
        
        existingPlan.setName(name);
        return mealPlanRepository.save(existingPlan);
    }
    
    public void deleteMealPlan(Long id) {
        mealPlanRepository.deleteById(id);
    }
    
    public void replaceDayMeals(Long mealPlanId, Integer dayOfWeek, java.util.List<org.uvhnael.mpbe.model.MealPlanItem> newItems) {
        MealPlan mealPlan = mealPlanRepository.findById(mealPlanId)
            .orElseThrow(() -> new RuntimeException("Meal plan not found"));
        
        // Remove old items for this day
        mealPlan.getMealPlanItems().removeIf(item -> item.getDayOfWeek().equals(dayOfWeek));
        
        // Add new items
        for (org.uvhnael.mpbe.model.MealPlanItem item : newItems) {
            item.setMealPlan(mealPlan);
            item.setDayOfWeek(dayOfWeek);
            mealPlan.getMealPlanItems().add(item);
        }
        
        mealPlanRepository.save(mealPlan);
    }
}
