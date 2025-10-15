package com.haidev.profileservice.mapper;

import com.haidev.profileservice.dto.request.UserProfileCreationRequest;
import com.haidev.profileservice.dto.response.UserProfileResponse;
import com.haidev.profileservice.entity.UserProfile;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {
    UserProfile toUserProfile(UserProfileCreationRequest request);
    UserProfileResponse toUserProfileResponse(UserProfile userProfile);

}
