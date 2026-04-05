package com.finance.platform.service.impl;

import com.finance.platform.dto.CreateUserRequest;
import com.finance.platform.dto.UpdateUserRequest;
import com.finance.platform.dto.UserResponse;
import com.finance.platform.entity.User;
import com.finance.platform.enums.Role;
import com.finance.platform.enums.UserStatus;
import com.finance.platform.exception.DuplicateResourceException;
import com.finance.platform.exception.ResourceNotFoundException;
import com.finance.platform.repository.UserRepository;
import com.finance.platform.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Default implementation of user management operations.
 */
@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException(
                    "User with email '" + request.getEmail() + "' already exists");
        }

        User user = new User(
                request.getName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getRole()
        );

        User saved = userRepository.save(user);

        log.info("Created user: id={}, email={}, role={}",
                saved.getId(), saved.getEmail(), saved.getRole());

        return UserResponse.from(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = findUserOrThrow(id);
        return UserResponse.from(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        User user = findUserOrThrow(id);

        if (!user.getEmail().equals(request.getEmail())
                && userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException(
                    "Email '" + request.getEmail() + "' is already in use");
        }

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        User saved = userRepository.save(user);
        log.info("Updated user: id={}, email={}", saved.getId(), saved.getEmail());

        return UserResponse.from(saved);
    }

    @Override
    @Transactional
    public UserResponse changeRole(Long id, Role newRole) {
        User user = findUserOrThrow(id);
        Role oldRole = user.getRole();
        user.setRole(newRole);

        User saved = userRepository.save(user);

        log.info("Role changed: userId={}, from={}, to={}",
                saved.getId(), oldRole, newRole);

        return UserResponse.from(saved);
    }

    @Override
    @Transactional
    public UserResponse changeStatus(Long id, UserStatus newStatus) {
        User user = findUserOrThrow(id);
        UserStatus oldStatus = user.getStatus();
        user.setStatus(newStatus);

        User saved = userRepository.save(user);

        log.info("Status changed: userId={}, from={}, to={}",
                saved.getId(), oldStatus, newStatus);

        return UserResponse.from(saved);
    }

    /**
     * Loads a user or throws a not-found exception.
     */
    private User findUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + id));
    }
}
