package com.finance.platform.security;

import com.finance.platform.entity.User;
import com.finance.platform.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Resolves the current authenticated user from the security context.
 */
@Component
public class AuthenticatedUserProvider {

    private final UserRepository userRepository;

    public AuthenticatedUserProvider(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Returns the database ID of the current authenticated user.
     */
    public Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user found in security context");
        }

        String email = Objects.toString(auth.getName(), "").trim();
        if (email.isEmpty() || "anonymousUser".equals(email)) {
            throw new IllegalStateException("Authenticated user email is unavailable in security context");
        }

        return userRepository.findByEmail(email)
                .map(User::getId)
                .orElseThrow(() -> new IllegalStateException(
                        "Authenticated user not found in database: " + email));
    }
}
