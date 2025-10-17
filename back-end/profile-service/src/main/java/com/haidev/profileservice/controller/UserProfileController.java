package com.haidev.profileservice.controller;

import com.haidev.profileservice.dto.request.UserProfileCreationRequest;
import com.haidev.profileservice.dto.response.UserProfileResponse;
import com.haidev.profileservice.service.UserProfileService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserProfileController {
    UserProfileService userProfileService;

    @GetMapping("/{profileId}")
    public UserProfileResponse getUserProfile(@PathVariable String profileId) {
        return userProfileService.getUserProfile(profileId);
    }

    @GetMapping
    public List<UserProfileResponse> getAllUserProfiles() {
        return userProfileService.getAllUserProfiles();
    }
}
