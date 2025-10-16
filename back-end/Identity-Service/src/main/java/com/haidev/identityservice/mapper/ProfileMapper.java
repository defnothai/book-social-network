package com.haidev.identityservice.mapper;

import com.haidev.identityservice.dto.request.profile.ProfileCreationRequest;
import com.haidev.identityservice.dto.request.user.UserCreationRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
    ProfileCreationRequest toProfileCreationRequest(UserCreationRequest request);
}
