package com.haidev.identityservice.dto.request.permission;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PermissionCreateRequest {

    String name;
    String description;
}
