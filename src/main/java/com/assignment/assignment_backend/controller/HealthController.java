package com.assignment.assignment_backend.controller;

import com.assignment.assignment_backend.service.Neo4jService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HealthController {

    private final Neo4jService neo4jService;

    public HealthController(Neo4jService neo4jService) {
        this.neo4jService = neo4jService;
    }

    @GetMapping("/api/health")
    public Map<String, Object> health() {
        boolean connectivity = neo4jService.isDriverConnected();
        String message = connectivity ? neo4jService.pingDatabase() : null;

        return Map.of(
            "app", "ok",
            "neo4jConnectivity", connectivity,
            "neo4jMessage", message == null ? "unavailable" : message
        );
    }
}