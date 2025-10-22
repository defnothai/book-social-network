package com.haidev.profileservice.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
@AllArgsConstructor
public enum ErrorCode {

    // USER
    USER_EXISTED(1001, "User already exists", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1002, "Username must be at least {min} characters long", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1003, "Password must be at least {min} characters long", HttpStatus.BAD_REQUEST),
    FIRSTNAME_INVALID(1004, "First name must be at least {min} characters long", HttpStatus.BAD_REQUEST),
    LASTNAME_INVALID(1005, "Last name must be at least {min} characters long", HttpStatus.BAD_REQUEST),
    DOB_INVALID(1006, "Date of birth must be in the past", HttpStatus.BAD_REQUEST),
    DOB_NOT_NULL(1007, "Date of birth is required", HttpStatus.BAD_REQUEST),
    DOB_CONSTRAINT(1012, "Your age must be at least {min}", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1009, "User not existed", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1010, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1011, "You do not have permission", HttpStatus.FORBIDDEN),

    // PERMISSION
    PERMISSION_NOT_EXISTED(1101, "Permission not existed", HttpStatus.NOT_FOUND),

    // ROLE
    ROLE_NOT_EXISTED(1201, "Role not existed", HttpStatus.NOT_FOUND),

    // OTHERS
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized exception", HttpStatus.INTERNAL_SERVER_ERROR),
    ;

    int code;
    String message;
    HttpStatus httpStatus;
}
