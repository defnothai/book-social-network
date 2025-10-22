package com.haidev.identityservice.repository.httpclient;

import com.haidev.identityservice.configuration.AuthenticationRequestInterceptor;
import com.haidev.identityservice.dto.request.profile.ProfileCreationRequest;
import com.haidev.identityservice.dto.response.UserProfileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

// FeignClient đánh dấu báo cho spring biết đây là một rest client interface
// nó đánh dấu cho spring hiểu rằng tất cả các phương thức bên trong là những lời gọi api đến service khác
// khi spring khởi động, Spring Cloud OpenFeign quét tất cả các interface được đánh dấu @FeignClient rồi
/*
    1. tạo ra các proxy class (class giao tiếp với service khác) thay thế interface này.
    2. proxy đó có logic: khi gọi hàm trong interface này thì nó sẽ gửi một request đến http
        đến url mà đã khai báo trong @FeignClient
    3. nhìn vào tên hàm, tham số, kiểu trả về để suy ra method, path, body, kiểu trả về của request
*/
@FeignClient(
        name = "${app.services.profile.name}",
        url = "${app.services.profile.url}",
        configuration = { AuthenticationRequestInterceptor.class }
)
public interface ProfileClient {
    @PostMapping(value = "/internal/users", produces = MediaType.APPLICATION_JSON_VALUE)
    UserProfileResponse createProfile(@RequestBody ProfileCreationRequest request);
}
