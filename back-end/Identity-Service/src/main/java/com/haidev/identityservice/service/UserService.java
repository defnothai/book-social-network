package com.haidev.identityservice.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.haidev.identityservice.dto.request.profile.ProfileCreationRequest;
import com.haidev.identityservice.entity.Role;
import com.haidev.identityservice.mapper.ProfileMapper;
import com.haidev.identityservice.repository.httpclient.ProfileClient;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.haidev.identityservice.dto.request.user.UserCreationRequest;
import com.haidev.identityservice.dto.request.user.UserUpdateRequest;
import com.haidev.identityservice.dto.response.UserResponse;
import com.haidev.identityservice.entity.User;
import com.haidev.identityservice.exception.AppException;
import com.haidev.identityservice.exception.ErrorCode;
import com.haidev.identityservice.mapper.UserMapper;
import com.haidev.identityservice.repository.RoleRepository;
import com.haidev.identityservice.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class UserService {

    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;
    ProfileClient profileClient;
    ProfileMapper profileMapper;

    public UserResponse createUser(UserCreationRequest request) {

        log.info("Service: create user");

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        Set<Role> roles = new HashSet<>();
        roles.add(
                roleRepository.findById(
                        com.haidev.identityservice.enums.Role.USER.name())
                        .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED)
                        )
        );
        user.setRoles(roles);
        user = userRepository.save(user);
        ProfileCreationRequest profileRequest = profileMapper.toProfileCreationRequest(request);
        profileRequest.setUserId(user.getId());
//        ServletRequestAttributes servletRequestAttributes =
//                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//        var authHeader = servletRequestAttributes.getRequest().getHeader("Authorization");
//        log.info("Auth header: {}", authHeader);
        var profileResponse = profileClient.createProfile(profileRequest);
        log.info("Profile response: {}", profileResponse);
        return userMapper.toUserResponse(user);
    }

//    @PreAuthorize("hasAuthority('APPROVE_POST')")
//    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getUsers() {
        return userRepository.findAll().stream().map(userMapper::toUserResponse).toList();
    }

    @PostAuthorize("returnObject.username == authentication.name")
    public UserResponse getUserById(String id) {
        User user = userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return userMapper.toUserResponse(user);
    }

    public UserResponse updateUser(String id, UserUpdateRequest request) {
        User user = userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        userMapper.updateUser(request, user);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        List<com.haidev.identityservice.entity.Role> roles = roleRepository.findAllById(request.getRoles());
        user.setRoles(new HashSet<>(roles));
        return userMapper.toUserResponse(userRepository.save(user));
    }

    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }

    public UserResponse getMyInfo() {
        SecurityContext contextHolder = SecurityContextHolder.getContext();
        String username = contextHolder.getAuthentication().getName();
        User user =
                userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return userMapper.toUserResponse(user);
    }
}
