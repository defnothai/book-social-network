package com.haidev.identityservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.haidev.identityservice.dto.request.role.RoleCreateRequest;
import com.haidev.identityservice.dto.request.role.RoleUpdateRequest;
import com.haidev.identityservice.dto.response.RoleResponse;
import com.haidev.identityservice.entity.Role;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleCreateRequest request);

    RoleResponse toRoleResponse(Role role);

    @Mapping(target = "permissions", ignore = true)
    void updateRole(RoleUpdateRequest request, @MappingTarget Role role);
}
