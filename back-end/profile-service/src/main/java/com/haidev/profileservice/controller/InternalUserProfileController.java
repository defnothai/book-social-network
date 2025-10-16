package com.haidev.profileservice.controller;

import com.haidev.profileservice.dto.request.UserProfileCreationRequest;
import com.haidev.profileservice.dto.response.UserProfileResponse;
import com.haidev.profileservice.service.UserProfileService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/internal/users")
@RequiredArgsConstructor
public class InternalUserProfileController {
    UserProfileService userProfileService;

    @PostMapping
    public UserProfileResponse createUserProfile(@RequestBody UserProfileCreationRequest request) {
        return userProfileService.createUserProfile(request);
    }

}
