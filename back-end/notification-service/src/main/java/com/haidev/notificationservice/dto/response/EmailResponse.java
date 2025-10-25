package com.haidev.notificationservice.dto.response;

import com.haidev.notificationservice.dto.request.Recipient;
import com.haidev.notificationservice.dto.request.Sender;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmailResponse {
    String messageId;
}
