package com.haidev.identityservice.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.haidev.identityservice.dto.request.permission.PermissionCreateRequest;
import com.haidev.identityservice.dto.request.permission.PermissionUpdateRequest;
import com.haidev.identityservice.dto.response.PermissionResponse;
import com.haidev.identityservice.entity.Permission;
import com.haidev.identityservice.exception.AppException;
import com.haidev.identityservice.exception.ErrorCode;
import com.haidev.identityservice.mapper.PermissionMapper;
import com.haidev.identityservice.repository.PermissionRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class PermissionService {

    PermissionRepository permissionRepository;
    PermissionMapper permissionMapper;

    public PermissionResponse create(PermissionCreateRequest request) {
        return permissionMapper.toPermissionResponse(permissionRepository.save(permissionMapper.toPermission(request)));
    }

    public List<PermissionResponse> getAll() {
        return permissionRepository.findAll().stream()
                .map(permissionMapper::toPermissionResponse)
                .toList();
    }

    public PermissionResponse getById(String id) {
        return permissionMapper.toPermissionResponse(permissionRepository
                .findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PERMISSION_NOT_EXISTED)));
    }

    public void deleteById(String id) {
        permissionRepository.deleteById(id);
    }

    public PermissionResponse update(String id, PermissionUpdateRequest request) {
        Permission permission =
                permissionRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.PERMISSION_NOT_EXISTED));
        permissionMapper.updatePermission(request, permission);
        return permissionMapper.toPermissionResponse(permissionRepository.save(permission));
    }
}
