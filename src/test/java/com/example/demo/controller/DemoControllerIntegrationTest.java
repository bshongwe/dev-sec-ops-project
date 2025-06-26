package com.example.demo.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class DemoControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/v1";
    }

    @Test
    void testHomeEndpointIntegration() {
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                baseUrl + "/", 
                HttpMethod.GET, 
                null, 
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Hello DevSecOps World!", response.getBody().get("message"));
        assertEquals("1.0.0", response.getBody().get("version"));
        assertEquals("healthy", response.getBody().get("status"));
        assertNotNull(response.getBody().get("timestamp"));
    }

    @Test
    void testHealthEndpointIntegration() {
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                baseUrl + "/health", 
                HttpMethod.GET, 
                null, 
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("UP", response.getBody().get("status"));
        assertNotNull(response.getBody().get("timestamp"));
    }

    @Test
    void testActuatorHealthIntegration() {
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                "http://localhost:" + port + "/actuator/health", 
                HttpMethod.GET, 
                null, 
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("UP", response.getBody().get("status"));
    }
}
