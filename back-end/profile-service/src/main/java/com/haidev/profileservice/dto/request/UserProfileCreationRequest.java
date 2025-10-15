package com.haidev.profileservice.dto.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserProfileCreationRequest {
    String firstName;
    String lastName;
    LocalDate dob;
    String city;
}
