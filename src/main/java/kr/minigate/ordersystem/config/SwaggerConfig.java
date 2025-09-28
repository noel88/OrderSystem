package kr.minigate.ordersystem.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Order System API")
                        .description("""
                                ## 주문 시스템 REST API

                                이 API는 온라인 주문 시스템을 위한 RESTful 웹 서비스입니다.

                                ### 주요 기능
                                - **회원 관리**: 회원 등록, 조회, 수정, 삭제
                                - **상품 관리**: 상품 등록, 조회, 재고 관리
                                - **주문 관리**: 주문 생성, 조회, 상태 변경, 취소
                                - **결제 관리**: 결제 처리, 조회, 환불, 취소

                                ### 인증
                                현재 버전에서는 별도의 인증이 필요하지 않습니다.

                                ### 응답 형식
                                모든 API는 JSON 형식으로 응답합니다.

                                ### 에러 코드
                                - **400**: 잘못된 요청 (Bad Request)
                                - **404**: 리소스를 찾을 수 없음 (Not Found)
                                - **500**: 서버 내부 오류 (Internal Server Error)
                                """)
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Order System Team")
                                .email("support@ordersystem.com")
                                .url("https://github.com/minigate/order-system"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("개발 서버"),
                        new Server()
                                .url("https://api.ordersystem.com")
                                .description("운영 서버")
                ));
    }
}