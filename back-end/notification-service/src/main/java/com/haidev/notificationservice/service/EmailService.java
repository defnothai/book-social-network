package com.haidev.notificationservice.service;
import com.haidev.notificationservice.dto.request.EmailRequest;
import com.haidev.notificationservice.dto.request.SendEmailRequest;
import com.haidev.notificationservice.dto.request.Sender;
import com.haidev.notificationservice.dto.response.EmailResponse;
import com.haidev.notificationservice.exception.AppException;
import com.haidev.notificationservice.exception.ErrorCode;
import com.haidev.notificationservice.repository.httpclient.EmailClient;
import feign.FeignException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailService {
    EmailClient emailClient;

    String apiKey = "SG.xxxxxxx.yyyyyyy"; // Replace with your actual SendGrid API key

    public EmailResponse sendEmail(SendEmailRequest request) {
        EmailRequest emailRequest = EmailRequest.builder()
                .sender(Sender.builder()
                        .name("HaiDev")
                        .email("honghaiit257@gmail.com")
                        .build())
                .to(List.of(request.getTo()))
                .subject(request.getSubject())
                .htmlContent(request.getHtmlContent())
                .build();
        try {
            return emailClient.sendEmail(apiKey, emailRequest);
        } catch (FeignException e){
            throw new AppException(ErrorCode.CANNOT_SEND_EMAIL);
        }
    }
}
