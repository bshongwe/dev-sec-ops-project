package com.example.demo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Demo Controller", description = "DevSecOps Demo API endpoints")
public class DemoController {

    private static final Logger logger = LoggerFactory.getLogger(DemoController.class);

    @GetMapping("/")
    @Operation(summary = "Welcome endpoint", description = "Returns a welcome message")
    @ApiResponse(responseCode = "200", description = "Success")
    public ResponseEntity<Map<String, Object>> home() {
        logger.info("Home endpoint accessed");
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Hello DevSecOps World!");
        response.put("timestamp", LocalDateTime.now());
        response.put("version", "1.0.0");
        response.put("status", "healthy");
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Returns application health status")
    public ResponseEntity<Map<String, String>> health() {
        logger.debug("Health check endpoint accessed");
        
        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now().toString());
        
        return ResponseEntity.ok(health);
    }

    @PostMapping("/echo")
    @Operation(summary = "Echo message", description = "Echoes back the provided message with security validation")
    public ResponseEntity<Map<String, Object>> echo(@Valid @RequestBody EchoRequest request) {
        logger.info("Echo endpoint accessed with message length: {}", 
                   request.getMessage() != null ? request.getMessage().length() : 0);
        
        Map<String, Object> response = new HashMap<>();
        response.put("originalMessage", request.getMessage());
        response.put("echoMessage", "Echo: " + request.getMessage());
        response.put("timestamp", LocalDateTime.now());
        response.put("messageLength", request.getMessage().length());
        
        return ResponseEntity.ok(response);
    }

    // Inner class for request validation
    public static class EchoRequest {
        @NotBlank(message = "Message cannot be blank")
        @Size(min = 1, max = 1000, message = "Message must be between 1 and 1000 characters")
        private String message;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
