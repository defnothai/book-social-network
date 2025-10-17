package com.haidev.profileservice.service;

import com.haidev.profileservice.dto.request.UserProfileCreationRequest;
import com.haidev.profileservice.dto.response.UserProfileResponse;
import com.haidev.profileservice.mapper.UserProfileMapper;
import com.haidev.profileservice.repository.UserProfileRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class UserProfileService {
    UserProfileRepository userProfileRepository;
    UserProfileMapper userProfileMapper;

    public UserProfileResponse createUserProfile(UserProfileCreationRequest request) {
        return userProfileMapper.toUserProfileResponse(
                userProfileRepository.save(userProfileMapper.toUserProfile(request))
        );
    }

    public UserProfileResponse getUserProfile(String id) {
        return userProfileMapper.toUserProfileResponse(
                userProfileRepository.findById(id).orElseThrow(() -> new RuntimeException("User profile not found"))
        );
    }

    public List<UserProfileResponse> getAllUserProfiles() {
        return userProfileRepository
                .findAll()
                .stream()
                .map(userProfileMapper::toUserProfileResponse)
                .toList();
    }
}
