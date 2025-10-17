package com.haidev.apigateway.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.haidev.apigateway.dto.response.ApiResponse;
import com.haidev.apigateway.service.IdentityService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationFilter implements GlobalFilter, Ordered {

    IdentityService identityService;
    ObjectMapper objectMapper;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("Enter Authentication filter....");
        // Get token from Authorization header
        List<String> authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION);
        if (CollectionUtils.isEmpty(authHeader)) {
            return unauthenticated(exchange.getResponse());
        }
        String token = authHeader.getFirst().replace("Bearer ", "");
        log.info("Token: {}", token);
        // Verify token
        return identityService.introspect(token)
                .flatMap(introspectResponseApiResponse -> {
            if (introspectResponseApiResponse.getResult().isValid()) {
                log.info("Token is valid");
                return chain.filter(exchange);
            } else {
                log.info("Token is invalid");
                return unauthenticated(exchange.getResponse());
            }
        }).onErrorResume(throwable -> {;
            log.error("Error introspecting token: {}", throwable.getMessage());
            return unauthenticated(exchange.getResponse());
        });
    }

    @Override
    public int getOrder() {
        return -1;
    }

    Mono<Void> unauthenticated(ServerHttpResponse response) {
        ApiResponse<?> apiResponse = ApiResponse.<Object>builder()
                .code(1401)
                .message("Unauthenticated")
                .build();

        String body = null;
        try {
            body = objectMapper.writeValueAsString(apiResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        return response.writeWith(
                Mono.just(
                        response.bufferFactory().wrap(body.getBytes())
                )
        );
    }
}
