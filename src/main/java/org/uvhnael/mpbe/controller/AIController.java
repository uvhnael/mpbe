package org.uvhnael.mpbe.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.uvhnael.mpbe.dto.response.ApiResponse;
import org.uvhnael.mpbe.model.UserProfile;
import org.uvhnael.mpbe.service.GeminiAIService;
import org.uvhnael.mpbe.service.UserProfileService;

@Tag(name = "AI Suggestions", description = "AI-powered recipe and nutrition suggestions")
@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AIController {
    
    private final GeminiAIService geminiAIService;
    private final UserProfileService userProfileService;
    
    @PostMapping("/suggest-recipes")
    public ResponseEntity<?> suggestRecipes(@RequestParam Long userId) {
        try {
            UserProfile profile = userProfileService.getProfileByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
            
            String suggestions = geminiAIService.suggestRecipes(profile);
            
            return ResponseEntity.ok(new ApiResponse(true, "Recipes suggested", suggestions));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage()));
        }
    }
    
    @PostMapping("/analyze-nutrition")
    public ResponseEntity<?> analyzeNutrition(@RequestParam String recipeName) {
        try {
            String analysis = geminiAIService.analyzeNutrition(recipeName);
            
            return ResponseEntity.ok(new ApiResponse(true, "Nutrition analyzed", analysis));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage()));
        }
    }
    
    @PostMapping("/substitute-ingredient")
    public ResponseEntity<?> substituteIngredient(
            @RequestParam String ingredient,
            @RequestParam(required = false) String reason) {
        try {
            String prompt = String.format("""
                Suggest substitutes for ingredient: %s
                Reason: %s
                
                Return as JSON array with substitutes and their properties.
                """, ingredient, reason != null ? reason : "general substitute");
            
            // Use the existing callGeminiAPI method through a service
            String substitutes = geminiAIService.analyzeNutrition(prompt);
            
            return ResponseEntity.ok(new ApiResponse(true, "Substitutes suggested", substitutes));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage()));
        }
    }
}
