package com.haidev.apigateway.repository;

import com.haidev.apigateway.dto.request.IntrospectRequest;
import com.haidev.apigateway.dto.response.ApiResponse;
import com.haidev.apigateway.dto.response.IntrospectResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.PostExchange;
import reactor.core.publisher.Mono;

@Repository
public interface IdentityClient {

    // Dùng httpClient của spring 6 chứ không dùng feign client nữa
    @PostExchange(url = "/auth/introspect", contentType = MediaType.APPLICATION_JSON_VALUE)
    Mono<ApiResponse<IntrospectResponse>> introspect(@RequestBody IntrospectRequest request);
}
