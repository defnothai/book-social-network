package com.haidev.identityservice.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import com.haidev.identityservice.dto.request.user.UserCreationRequest;
import com.haidev.identityservice.dto.response.UserResponse;
import com.haidev.identityservice.entity.User;
import com.haidev.identityservice.exception.AppException;
import com.haidev.identityservice.repository.UserRepository;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@SpringBootTest
@FieldDefaults(level = AccessLevel.PRIVATE)
@TestPropertySource("/test.properties")
public class UserServiceTest {

    @Autowired
    UserService userService;

    @Mock
    UserRepository userRepository;

    User user;
    UserCreationRequest request;
    UserResponse response;

    @BeforeEach
    void initData() {
        LocalDate dob = LocalDate.of(1990, 1, 1);
        request = UserCreationRequest.builder()
                .username("john")
                .password("12345678")
                .firstName("John")
                .lastName("Doe")
                .dob(dob)
                .build();
        response = UserResponse.builder()
                .id("cf345678")
                .username("john")
                .build();
        user = User.builder()
                .id("cf345678")
                .username("john")
                .build();
    }

    @Test
    void createUser_validRequest_success() {
        // given
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.save(any())).thenReturn(user);
        // when
        UserResponse userResponse = userService.createUser(request);
        // then
        Assertions.assertThat(userResponse.getId()).isEqualTo(response.getId());
        Assertions.assertThat(userResponse.getUsername()).isEqualTo(response.getUsername());
    }

    @Test
    void createUser_userExisted_fail() {
        // given
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        // when
        AppException exception = assertThrows(AppException.class, () -> userService.createUser(request));
        Assertions.assertThat(exception.getErrorCode().getCode()).isEqualTo(1001);
    }
}
