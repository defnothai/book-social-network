package com.haidev.notificationservice.controller;

import com.haidev.notificationservice.dto.request.SendEmailRequest;
import com.haidev.notificationservice.dto.response.ApiResponse;
import com.haidev.notificationservice.dto.response.EmailResponse;
import com.haidev.notificationservice.service.EmailService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/email")
public class EmailController {
    EmailService emailService;

    @PostMapping("/send")
    public ApiResponse<EmailResponse> sendEmail(@RequestBody SendEmailRequest request) {
        return ApiResponse.<EmailResponse>builder()
                .result(emailService.sendEmail(request))
                .build();
    }

    @KafkaListener(topics = "onboard-successful")
    public void listen(String message) {
        log.info("Received message from topic 'onboard-successful': {}", message);
    }

}
