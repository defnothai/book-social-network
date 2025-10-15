package com.haidev.identityservice.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.haidev.identityservice.dto.request.user.UserCreationRequest;
import com.haidev.identityservice.dto.request.user.UserUpdateRequest;
import com.haidev.identityservice.dto.response.ApiResponse;
import com.haidev.identityservice.dto.response.UserResponse;
import com.haidev.identityservice.entity.User;
import com.haidev.identityservice.service.UserService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    UserService userService;

    @PostMapping
    public ApiResponse<UserResponse> createUser(@Valid @RequestBody UserCreationRequest request) {
        log.info("Controller: create user");
        return ApiResponse.<UserResponse>builder()
                .result(userService.createUser(request))
                .build();
    }

    @GetMapping
    public ApiResponse<List<UserResponse>> getAllUsers() {
        log.info("Get all users");
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        log.info("User: {}", authentication.getName());
        authentication.getAuthorities().forEach(a -> log.info("Role: {}", a.getAuthority()));
        log.info("Roles: {}", authentication.getAuthorities());

        return ApiResponse.<List<UserResponse>>builder()
                .result(userService.getUsers())
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<UserResponse> getUserById(@PathVariable("id") String id) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getUserById(id))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<UserResponse> updateUser(
            @PathVariable("id") String id, @Valid @RequestBody UserUpdateRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.updateUser(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<User> deleteUser(@PathVariable("id") String id) {
        userService.deleteUser(id);
        return ApiResponse.<User>builder().build();
    }

    @GetMapping("/my-info")
    public ApiResponse<UserResponse> getMyInfo() {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getMyInfo())
                .build();
    }
}
