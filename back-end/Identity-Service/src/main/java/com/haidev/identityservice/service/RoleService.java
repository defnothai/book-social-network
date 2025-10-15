package com.haidev.identityservice.service;

import java.util.HashSet;
import java.util.List;

import org.springframework.stereotype.Service;

import com.haidev.identityservice.dto.request.role.RoleCreateRequest;
import com.haidev.identityservice.dto.request.role.RoleUpdateRequest;
import com.haidev.identityservice.dto.response.RoleResponse;
import com.haidev.identityservice.entity.Permission;
import com.haidev.identityservice.entity.Role;
import com.haidev.identityservice.exception.AppException;
import com.haidev.identityservice.exception.ErrorCode;
import com.haidev.identityservice.mapper.RoleMapper;
import com.haidev.identityservice.repository.PermissionRepository;
import com.haidev.identityservice.repository.RoleRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class RoleService {

    RoleRepository roleRepository;
    RoleMapper roleMapper;
    PermissionRepository permissionRepository;

    public void deleteById(String id) {
        roleRepository.deleteById(id);
    }

    public RoleResponse create(RoleCreateRequest request) {
        Role role = roleMapper.toRole(request);
        List<Permission> permissions = permissionRepository.findAllById(request.getPermissions());
        role.setPermissions(new HashSet<>(permissions));
        return roleMapper.toRoleResponse(roleRepository.save(role));
    }

    public List<RoleResponse> getAll() {
        return roleRepository.findAll().stream().map(roleMapper::toRoleResponse).toList();
    }

    public RoleResponse update(String id, RoleUpdateRequest request) {
        Role role = roleRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));
        roleMapper.updateRole(request, role);
        List<Permission> permissions = permissionRepository.findAllById(request.getPermissions());
        role.setPermissions(new HashSet<>(permissions));
        return roleMapper.toRoleResponse(roleRepository.save(role));
    }
}
