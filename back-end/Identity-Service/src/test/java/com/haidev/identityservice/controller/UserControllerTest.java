package com.haidev.identityservice.controller;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.haidev.identityservice.dto.request.user.UserCreationRequest;
import com.haidev.identityservice.dto.response.UserResponse;
import com.haidev.identityservice.service.UserService;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
@AutoConfigureMockMvc // cho phép dùng MockMvc để giả lập request đến api
@FieldDefaults(level = AccessLevel.PRIVATE)
@TestPropertySource("/test.properties")
@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    static final String BASE_URL = "/api/users";

    @Autowired
    MockMvc mockMvc; // công cụ để giả lập request HTTP (GET, POST,...) đến controller.

    @Mock
    UserService userService; // tạo mock cho service

    @InjectMocks
    UserController userController;

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
                .firstName("John")
                .lastName("Doe")
                .dob(dob)
                .build();
    }

    @Test
    void createUser_validRequest_success() throws Exception {
        // * given
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(request);

        // * when, then
        // mock service
        Mockito.when(userService.createUser(ArgumentMatchers.any())).thenReturn(response);
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("code")
                        .value(1000)); // kĩ thì có thể expect thêm username, id, lastname, ...
    }

    @Test
    void createUser_usernameInvalid_fail() throws Exception {
        // * given
        request.setUsername("hai");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(request);

        // * when, then

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1002))
                .andExpect(
                        MockMvcResultMatchers.jsonPath("message").value("Username must be at least 4 characters long"));
    }
}
