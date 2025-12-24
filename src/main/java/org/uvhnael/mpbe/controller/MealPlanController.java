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
            
            // Generate default name based on dates and days
            String defaultName = String.format("%d-Day Meal Plan (%s)", 
                request.getDays(), 
                java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy")));
            mealPlan.setName(defaultName);
            
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
    @Operation(summary = "Get user meal plans with pagination", description = "Retrieve meal plans for a user with pagination support")
    public ResponseEntity<?> getUserMealPlans(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        try {
            org.springframework.data.domain.Sort.Direction direction = 
                sortDirection.equalsIgnoreCase("asc") ? 
                org.springframework.data.domain.Sort.Direction.ASC : 
                org.springframework.data.domain.Sort.Direction.DESC;
            
            org.springframework.data.domain.Pageable pageable = 
                org.springframework.data.domain.PageRequest.of(page, size, 
                    org.springframework.data.domain.Sort.by(direction, sortBy));
            
            org.springframework.data.domain.Page<MealPlan> mealPlansPage = 
                mealPlanService.getUserMealPlans(userId, pageable);
            
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("content", mealPlansPage.getContent());
            response.put("currentPage", mealPlansPage.getNumber());
            response.put("totalItems", mealPlansPage.getTotalElements());
            response.put("totalPages", mealPlansPage.getTotalPages());
            response.put("pageSize", mealPlansPage.getSize());
            response.put("hasNext", mealPlansPage.hasNext());
            response.put("hasPrevious", mealPlansPage.hasPrevious());
            
            return ResponseEntity.ok(new ApiResponse(true, "Success", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage()));
        }
    }
    
    @GetMapping("/all")
    @Operation(summary = "Get all user meal plans", description = "Retrieve all meal plans for a user without pagination")
    public ResponseEntity<?> getAllUserMealPlans(@RequestParam Long userId) {
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
    
    @PatchMapping("/{id}/name")
    @Operation(summary = "Update meal plan name", description = "Update the name of a meal plan")
    public ResponseEntity<?> updateMealPlanName(
            @PathVariable Long id, 
            @RequestBody java.util.Map<String, String> request) {
        try {
            String newName = request.get("name");
            if (newName == null || newName.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Name cannot be empty"));
            }
            
            MealPlan updatedPlan = mealPlanService.updateMealPlanName(id, newName);
            return ResponseEntity.ok(new ApiResponse(true, "Meal plan name updated", updatedPlan));
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
