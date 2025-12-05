package org.uvhnael.mpbe.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.uvhnael.mpbe.dto.request.MealPlanRequest;
import org.uvhnael.mpbe.dto.response.ApiResponse;
import org.uvhnael.mpbe.model.MealPlan;
import org.uvhnael.mpbe.model.UserProfile;
import org.uvhnael.mpbe.service.GeminiAIService;
import org.uvhnael.mpbe.service.MealPlanService;
import org.uvhnael.mpbe.service.UserProfileService;

import java.util.List;

@Tag(name = "Meal Plans", description = "AI-powered meal planning APIs")
@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping("/meal-plans")
@RequiredArgsConstructor
public class MealPlanController {
    
    private final MealPlanService mealPlanService;
    private final UserProfileService userProfileService;
    private final GeminiAIService geminiAIService;
    private final org.uvhnael.mpbe.service.AIMealPlanParserService aiParserService;
    private final org.uvhnael.mpbe.repository.UserRepository userRepository;
    
    @PostMapping("/generate")
    public ResponseEntity<?> generateMealPlan(
            @RequestParam Long userId,
            @jakarta.validation.Valid @RequestBody MealPlanRequest request) {
        try {
            UserProfile profile = userProfileService.getProfileByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
            
            org.uvhnael.mpbe.model.User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Generate AI meal plan
            String aiResponse = geminiAIService.generateMealPlan(profile, request.getDays());
            
            // Create meal plan entity
            MealPlan mealPlan = new MealPlan();
            mealPlan.setUser(user);
            mealPlan.setStartDate(java.time.LocalDate.now());
            mealPlan.setEndDate(java.time.LocalDate.now().plusDays(request.getDays()));
            mealPlan.setStatus("active");
            
            // Parse AI response and save to database
            mealPlan = aiParserService.parseAndSaveMealPlan(aiResponse, mealPlan);
            mealPlan = mealPlanService.createMealPlan(mealPlan);
            
            return ResponseEntity.ok(new ApiResponse(true, "Meal plan generated and saved", mealPlan));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage()));
        }
    }
    
    @GetMapping
    public ResponseEntity<?> getUserMealPlans(@RequestParam Long userId) {
        try {
            List<MealPlan> mealPlans = mealPlanService.getUserMealPlans(userId);
            return ResponseEntity.ok(new ApiResponse(true, "Success", mealPlans));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getMealPlanById(@PathVariable Long id) {
        try {
            MealPlan mealPlan = mealPlanService.getMealPlanById(id)
                .orElseThrow(() -> new RuntimeException("Meal plan not found"));
            
            return ResponseEntity.ok(new ApiResponse(true, "Success", mealPlan));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMealPlan(@PathVariable Long id, @RequestBody MealPlan mealPlan) {
        try {
            MealPlan updatedPlan = mealPlanService.updateMealPlan(id, mealPlan);
            return ResponseEntity.ok(new ApiResponse(true, "Meal plan updated", updatedPlan));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMealPlan(@PathVariable Long id) {
        try {
            mealPlanService.deleteMealPlan(id);
            return ResponseEntity.ok(new ApiResponse(true, "Meal plan deleted"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage()));
        }
    }
    
    @PostMapping("/{id}/regenerate-day")
    public ResponseEntity<?> regenerateDay(
            @PathVariable Long id,
            @RequestParam Integer dayOfWeek) {
        try {
            MealPlan mealPlan = mealPlanService.getMealPlanById(id)
                .orElseThrow(() -> new RuntimeException("Meal plan not found"));
            
            UserProfile profile = userProfileService.getProfileByUserId(mealPlan.getUser().getId())
                .orElseThrow(() -> new RuntimeException("Profile not found"));
            
            // Generate AI meal plan for one day
            String aiResponse = geminiAIService.generateMealPlan(profile, 1);
            
            // Create temporary meal plan for parsing
            MealPlan tempPlan = new MealPlan();
            tempPlan.setUser(mealPlan.getUser());
            tempPlan = aiParserService.parseAndSaveMealPlan(aiResponse, tempPlan);
            
            // Delete old meals for this day and add new ones
            mealPlanService.replaceDayMeals(id, dayOfWeek, tempPlan.getMealPlanItems());
            
            return ResponseEntity.ok(new ApiResponse(true, "Day regenerated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage()));
        }
    }
}
