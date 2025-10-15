package com.haidev.identityservice.dto.response;

import java.util.Set;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class RoleResponse {
    String name;
    String description;
    Set<PermissionResponse> permissions;
}
