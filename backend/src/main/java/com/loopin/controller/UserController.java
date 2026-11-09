package com.loopin.controller;

import com.loopin.model.User;
import com.loopin.service.RequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Presentation Layer: REST Controller for Campus Users.
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final RequestService requestService;

    public UserController(RequestService requestService) {
        this.requestService = requestService;
    }

    /**
     * Endpoint: Retrieve all registered campus users
     * GET /api/users
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(requestService.getAllUsers());
    }

    /**
     * Endpoint: Retrieve single user by ID
     * GET /api/users/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(requestService.getUserById(id));
    }
}
