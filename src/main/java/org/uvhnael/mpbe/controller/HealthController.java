package org.uvhnael.mpbe.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.uvhnael.mpbe.dto.response.ApiResponse;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Tag(name = "Health", description = "Health check APIs")
@RestController
@RequestMapping("/health")
public class HealthController {

    @Operation(summary = "Check API health", description = "Returns the health status of the API")
    @GetMapping
    public ResponseEntity<?> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        health.put("service", "Meal Planner Backend");

        return ResponseEntity.ok(
                new ApiResponse(true, "Service is healthy", health)
        );
    }

    @Operation(summary = "Check API readiness", description = "Returns the readiness status of the API")
    @GetMapping("/ready")
    public ResponseEntity<?> readinessCheck() {
        Map<String, Object> readiness = new HashMap<>();
        readiness.put("ready", true);
        readiness.put("timestamp", LocalDateTime.now());

        return ResponseEntity.ok(
                new ApiResponse(true, "Service is ready", readiness)
        );
    }

    @Operation(summary = "Ping health check", description = "Simple ping-pong health check")
    @GetMapping("/ping")
    public ResponseEntity<?> ping() {
        Map<String, Object> pingResponse = new HashMap<>();
        pingResponse.put("response", "pong");
        pingResponse.put("timestamp", LocalDateTime.now());

        return ResponseEntity.ok(
                new ApiResponse(true, "Ping successful", pingResponse)
        );
    }
}