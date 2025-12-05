package org.uvhnael.mpbe.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.uvhnael.mpbe.dto.request.LoginRequest;
import org.uvhnael.mpbe.dto.request.RegisterRequest;
import org.uvhnael.mpbe.dto.response.ApiResponse;
import org.uvhnael.mpbe.dto.response.AuthResponse;
import org.uvhnael.mpbe.model.User;
import org.uvhnael.mpbe.service.AuthService;

@Tag(name = "Authentication", description = "Authentication management APIs")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    private final org.uvhnael.mpbe.security.JwtTokenProvider jwtTokenProvider;
    private final org.uvhnael.mpbe.service.RefreshTokenService refreshTokenService;
    
    @Operation(summary = "Register new user", description = "Create a new user account")
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            User user = authService.register(
                request.getEmail(),
                request.getPassword(),
                request.getFullName()
            );
            
            String accessToken = jwtTokenProvider.generateToken(user.getEmail());
            org.uvhnael.mpbe.model.RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
            
            AuthResponse response = new AuthResponse(
                accessToken,
                refreshToken.getToken(),
                user.getId(),
                user.getEmail(),
                user.getFullName()
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage()));
        }
    }
    
    @Operation(summary = "User login", description = "Authenticate user and return JWT token")
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            User user = authService.authenticate(request.getEmail(), request.getPassword());
            
            String accessToken = jwtTokenProvider.generateToken(user.getEmail());
            
            // Delete old refresh tokens for this user
            refreshTokenService.deleteByUser(user);
            org.uvhnael.mpbe.model.RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
            
            AuthResponse response = new AuthResponse(
                accessToken,
                refreshToken.getToken(),
                user.getId(),
                user.getEmail(),
                user.getFullName()
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage()));
        }
    }
    
    @Operation(summary = "Refresh token", description = "Refresh JWT access token")
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody java.util.Map<String, String> request) {
        try {
            String refreshTokenStr = request.get("refreshToken");
            if (refreshTokenStr == null || refreshTokenStr.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Refresh token is required"));
            }
            
            org.uvhnael.mpbe.model.RefreshToken refreshToken = refreshTokenService.findByToken(refreshTokenStr);
            refreshToken = refreshTokenService.verifyExpiration(refreshToken);
            
            User user = refreshToken.getUser();
            String newAccessToken = jwtTokenProvider.generateToken(user.getEmail());
            
            return ResponseEntity.ok(new ApiResponse(true, "Token refreshed", newAccessToken));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage()));
        }
    }
}
