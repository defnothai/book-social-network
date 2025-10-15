package com.haidev.identityservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.haidev.identityservice.dto.request.permission.PermissionCreateRequest;
import com.haidev.identityservice.dto.request.permission.PermissionUpdateRequest;
import com.haidev.identityservice.dto.response.PermissionResponse;
import com.haidev.identityservice.entity.Permission;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionCreateRequest request);

    PermissionResponse toPermissionResponse(Permission permission);

    void updatePermission(PermissionUpdateRequest request, @MappingTarget Permission permission);
}
