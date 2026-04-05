package com.finance.platform.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Update request for user profile fields (name, email).
 *
 * Note: Role and Status changes have dedicated endpoints (PATCH),
 * not bundled into a general update. This is deliberate — role
 * changes are a privileged operation that should be auditable
 * as a distinct event, not hidden inside a bulk profile update.
 */
public class UpdateUserRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    @Size(max = 150, message = "Email must not exceed 150 characters")
    private String email;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
