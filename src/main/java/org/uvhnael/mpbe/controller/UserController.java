package org.uvhnael.mpbe.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.uvhnael.mpbe.dto.request.UserProfileRequest;
import org.uvhnael.mpbe.dto.response.ApiResponse;
import org.uvhnael.mpbe.dto.response.UserProfileResponse;
import org.uvhnael.mpbe.model.User;
import org.uvhnael.mpbe.model.UserProfile;
import org.uvhnael.mpbe.service.UserProfileService;
import org.uvhnael.mpbe.service.UserService;

@Tag(name = "Users", description = "User profile management APIs")
@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    private final UserProfileService userProfileService;
    
    @GetMapping("/profile/{userId}")
    public ResponseEntity<?> getProfile(@PathVariable Long userId) {
        try {
            UserProfile profile = userProfileService.getProfileByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
            
            UserProfileResponse response = convertToResponse(profile);
            return ResponseEntity.ok(new ApiResponse(true, "Success", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage()));
        }
    }
    
    @PutMapping("/profile/{userId}")
    public ResponseEntity<?> updateProfile(
            @PathVariable Long userId,
            @jakarta.validation.Valid @RequestBody UserProfileRequest request) {
        try {
            User user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Get existing profile or create new one
            UserProfile profile = userProfileService.getProfileByUserId(userId)
                .orElse(new UserProfile());
            
            profile.setUser(user);
            if (request.getAge() != null) profile.setAge(request.getAge());
            if (request.getGender() != null) profile.setGender(request.getGender());
            if (request.getHeight() != null) profile.setHeight(request.getHeight());
            if (request.getWeight() != null) profile.setWeight(request.getWeight());
            if (request.getGoal() != null) profile.setGoal(request.getGoal());
            if (request.getActivityLevel() != null) profile.setActivityLevel(request.getActivityLevel());
            if (request.getDietaryPreference() != null) profile.setDietaryPreference(request.getDietaryPreference());
            if (request.getAllergies() != null) profile.setAllergies(request.getAllergies());
            if (request.getBudgetPerMeal() != null) profile.setBudgetPerMeal(request.getBudgetPerMeal());
            
            UserProfile savedProfile = userProfileService.saveProfile(profile);
            
            UserProfileResponse response = convertToResponse(savedProfile);
            return ResponseEntity.ok(new ApiResponse(true, "Profile updated", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage()));
        }
    }
    
    @PutMapping("/preferences/{userId}")
    public ResponseEntity<?> updatePreferences(
            @PathVariable Long userId,
            @jakarta.validation.Valid @RequestBody UserProfileRequest request) {
        try {
            UserProfile profile = userProfileService.getProfileByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
            
            if (request.getDietaryPreference() != null) profile.setDietaryPreference(request.getDietaryPreference());
            if (request.getAllergies() != null) profile.setAllergies(request.getAllergies());
            if (request.getBudgetPerMeal() != null) profile.setBudgetPerMeal(request.getBudgetPerMeal());
            
            UserProfile savedProfile = userProfileService.saveProfile(profile);
            
            UserProfileResponse response = convertToResponse(savedProfile);
            return ResponseEntity.ok(new ApiResponse(true, "Preferences updated", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage()));
        }
    }
    
    private UserProfileResponse convertToResponse(UserProfile profile) {
        UserProfileResponse response = new UserProfileResponse();
        response.setId(profile.getId());
        response.setUserId(profile.getUser() != null ? profile.getUser().getId() : null);
        response.setAge(profile.getAge());
        response.setGender(profile.getGender());
        response.setHeight(profile.getHeight());
        response.setWeight(profile.getWeight());
        response.setGoal(profile.getGoal());
        response.setActivityLevel(profile.getActivityLevel());
        response.setDietaryPreference(profile.getDietaryPreference());
        response.setAllergies(profile.getAllergies());
        response.setBudgetPerMeal(profile.getBudgetPerMeal());
        return response;
    }
}
