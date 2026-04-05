package com.finance.platform.service;

import com.finance.platform.dto.CreateUserRequest;
import com.finance.platform.dto.UpdateUserRequest;
import com.finance.platform.dto.UserResponse;
import com.finance.platform.enums.Role;
import com.finance.platform.enums.UserStatus;

import java.util.List;

/**
 * User management service contract.
 */
public interface UserService {

    UserResponse createUser(CreateUserRequest request);

    UserResponse getUserById(Long id);

    List<UserResponse> getAllUsers();

    UserResponse updateUser(Long id, UpdateUserRequest request);

    UserResponse changeRole(Long id, Role newRole);

    UserResponse changeStatus(Long id, UserStatus newStatus);
}
