package com.finance.platform.controller;

import com.finance.platform.dto.CreateUserRequest;
import com.finance.platform.dto.UpdateUserRequest;
import com.finance.platform.dto.UserResponse;
import com.finance.platform.enums.Role;
import com.finance.platform.enums.UserStatus;
import com.finance.platform.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Administrative endpoints for user management.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Creates a new user.
     */
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserResponse created = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * GET /api/users/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    /**
     * GET /api/users
     */
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    /**
     * Updates name and email for an existing user.
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    /**
     * Updates the role of an existing user.
     */
    @PatchMapping("/{id}/role")
    public ResponseEntity<UserResponse> changeRole(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        Role newRole = parseRole(body);
        return ResponseEntity.ok(userService.changeRole(id, newRole));
    }

    /**
     * Updates the status of an existing user.
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<UserResponse> changeStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        UserStatus newStatus = parseStatus(body);
        return ResponseEntity.ok(userService.changeStatus(id, newStatus));
    }

    private Role parseRole(Map<String, String> body) {
        String rawRole = extractRequiredField(body, "role");
        try {
            return Role.valueOf(rawRole.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid role. Allowed values: VIEWER, ANALYST, ADMIN");
        }
    }

    private UserStatus parseStatus(Map<String, String> body) {
        String rawStatus = extractRequiredField(body, "status");
        try {
            return UserStatus.valueOf(rawStatus.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid status. Allowed values: ACTIVE, INACTIVE");
        }
    }

    private String extractRequiredField(Map<String, String> body, String fieldName) {
        if (body == null) {
            throw new IllegalArgumentException("Request body is required");
        }

        String value = body.get(fieldName);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Field '" + fieldName + "' is required");
        }

        return value;
    }
}
