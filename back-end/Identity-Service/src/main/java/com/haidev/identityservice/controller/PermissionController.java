package com.haidev.identityservice.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.haidev.identityservice.dto.request.permission.PermissionCreateRequest;
import com.haidev.identityservice.dto.request.permission.PermissionUpdateRequest;
import com.haidev.identityservice.dto.response.ApiResponse;
import com.haidev.identityservice.dto.response.PermissionResponse;
import com.haidev.identityservice.service.PermissionService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/api/permissions")
public class PermissionController {

    PermissionService permissionService;

    @PostMapping
    public ApiResponse<PermissionResponse> create(@RequestBody PermissionCreateRequest request) {
        return ApiResponse.<PermissionResponse>builder()
                .result(permissionService.create(request))
                .build();
    }

    @GetMapping
    public ApiResponse<List<PermissionResponse>> getAll() {
        return ApiResponse.<List<PermissionResponse>>builder()
                .result(permissionService.getAll())
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteById(@PathVariable("id") String id) {
        permissionService.deleteById(id);
        return ApiResponse.<Void>builder().build();
    }

    @PutMapping("/{id}")
    public ApiResponse<PermissionResponse> update(
            @PathVariable("id") String id, @RequestBody PermissionUpdateRequest request) {
        return ApiResponse.<PermissionResponse>builder()
                .result(permissionService.update(id, request))
                .build();
    }
}
