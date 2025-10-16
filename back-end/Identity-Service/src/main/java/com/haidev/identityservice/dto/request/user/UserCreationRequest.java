package com.haidev.identityservice.dto.request.user;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import com.haidev.identityservice.validator.DobConstraint;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
public class UserCreationRequest {

    @Size(min = 4, message = "USERNAME_INVALID")
    String username;

    @Size(min = 8, message = "PASSWORD_INVALID")
    String password;

    @Size(min = 3, message = "FIRSTNAME_INVALID")
    String firstName;

    @Size(min = 3, message = "LASTNAME_INVALID")
    String lastName;

    @NotNull(message = "DOB_NOT_NULL")
    @Past(message = "DOB_INVALID")
    @DobConstraint(min = 16, message = "DOB_CONSTRAINT")
    LocalDate dob;

    String city;
}
