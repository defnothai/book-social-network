package com.haidev.identityservice.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.haidev.identityservice.dto.request.role.RoleCreateRequest;
import com.haidev.identityservice.dto.request.role.RoleUpdateRequest;
import com.haidev.identityservice.dto.response.ApiResponse;
import com.haidev.identityservice.dto.response.RoleResponse;
import com.haidev.identityservice.service.RoleService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleController {
    RoleService roleService;

    @PostMapping
    public ApiResponse<RoleResponse> create(@RequestBody RoleCreateRequest request) {
        return ApiResponse.<RoleResponse>builder()
                .result(roleService.create(request))
                .build();
    }

    @GetMapping
    public ApiResponse<List<RoleResponse>> getAll() {
        return ApiResponse.<List<RoleResponse>>builder()
                .result(roleService.getAll())
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteById(@PathVariable("id") String id) {
        roleService.deleteById(id);
        return ApiResponse.<Void>builder().build();
    }

    @PutMapping("/{id}")
    public ApiResponse<RoleResponse> update(@PathVariable("id") String id, @RequestBody RoleUpdateRequest request) {
        return ApiResponse.<RoleResponse>builder()
                .result(roleService.update(id, request))
                .build();
    }
}
