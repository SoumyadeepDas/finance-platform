package com.finance.platform.security;

import com.finance.platform.entity.User;
import com.finance.platform.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

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
        String email = auth.getName();

        return userRepository.findByEmail(email)
                .map(User::getId)
                .orElseThrow(() -> new IllegalStateException(
                        "Authenticated user not found in database: " + email));
    }
}
