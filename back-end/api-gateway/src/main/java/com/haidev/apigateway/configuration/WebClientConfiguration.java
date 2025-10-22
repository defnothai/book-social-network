package com.haidev.apigateway.configuration;

import com.haidev.apigateway.repository.IdentityClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
/**
    Cấu hình webclient và HttpServiceProxyFactory trong spring boot 6 để có thể gọi API
    giữa các microservice (ở đây là từ api-gateway đến identity service) mà không dùng open feign.
    + WebClient là HTTP client thực tế mà Spring sử dụng để gửi request đi giữa các service
        (ở đây là từ api-gateway đến identity service)
*/
@Configuration
public class WebClientConfiguration {

    @Bean
    WebClient webClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8080/identity") // định nghĩa url gốc identity service mà gateway sẽ gọi đến
                .build();
    }

    @Bean
    IdentityClient identityClient(WebClient webClient) {
        // tạo ra một factory có thể sinh ra proxy (client runtime)
        // cho các interface được annotate bằng @PostExchange, @GetExchange, ...
        HttpServiceProxyFactory httpServiceProxyFactory = HttpServiceProxyFactory
                .builderFor(WebClientAdapter.create(webClient)) // chuyển webclient thành adapter để httpServiceProxyFactory sử dụng
                .build();

        // từ interface IdentityClient, Spring sinh ra một proxy class tự động thực thi.
        return httpServiceProxyFactory.createClient(IdentityClient.class);
    }
}
/**
 * LUỒNG HOẠT ĐỘNG:
    1. webClient() -> tạo một instance WebClient đã gắn baseUrl
    2. identity(webClient) -> nhận WebClient vừa tạo -> tạo ra proxy object cho IdentityClient
    3. khi đó, spring context có 2 bean: webclient, IdentityClient   (proxy)
    4. khi sử dụng IdentityClient trong service thì tức là đang dùng IdentityClient (proxy) bên trên
    5. khi chạy identityClient.introspect(request), proxy intercept call introspect(request)
    6. proxy đọc metadata từ @PostExchange
    7. proxy sử dụng webclient mà truyền lúc tạo factory
    8. webclient build ra một request mà url ghép với baseUrl đã định nghĩa rồi gửi request với body
    9. Identity Service nhận được, xử lý và trả về json
    10. Webclient nhận response, parse thành ApiResponse<IntrospectResponse>
    11. Do introspect() đã định nghĩa interface trả về Mono<> nên dữ liệu được wrapped trong một reactive stream
 */
