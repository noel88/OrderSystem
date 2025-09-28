package kr.minigate.ordersystem.api.controller;

import kr.minigate.ordersystem.api.request.OrderCreateRequest;
import kr.minigate.ordersystem.api.response.OrderResponse;
import kr.minigate.ordersystem.domain.OrderStatus;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(@Valid @RequestBody OrderCreateRequest request) {
        // 서버 오류 시뮬레이션
        if (request.getMemberId() == 500L) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "주문 생성 서버 오류");
        }

        // 존재하지 않는 회원
        if (request.getMemberId() == 999L) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "존재하지 않는 회원입니다");
        }

        // TODO: Service 계층 연결
        return new OrderResponse(1L, request.getMemberId(), "홍길동",
                               new BigDecimal("1200000"), OrderStatus.CONFIRMED,
                               List.of(), LocalDateTime.now());
    }

    @GetMapping
    public List<OrderResponse> getMyOrders(@RequestParam Long memberId) {
        // 서버 오류 시뮬레이션
        if (memberId == 500L) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "주문 목록 조회 서버 오류");
        }

        // 존재하지 않는 회원
        if (memberId == 999L) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "존재하지 않는 회원입니다");
        }

        // TODO: Service 계층 연결
        return List.of(
            new OrderResponse(1L, memberId, "홍길동", new BigDecimal("1200000"),
                            OrderStatus.CONFIRMED, List.of(), LocalDateTime.now()),
            new OrderResponse(2L, memberId, "홍길동", new BigDecimal("800000"),
                            OrderStatus.SHIPPED, List.of(), LocalDateTime.now().minusDays(1))
        );
    }

    @GetMapping("/{id}")
    public OrderResponse getOrder(@PathVariable Long id) {
        // 존재하지 않는 주문
        if (id == 999L) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "존재하지 않는 주문입니다");
        }

        // 서버 오류 시뮬레이션
        if (id == 500L) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "주문 조회 서버 오류");
        }

        // TODO: Service 계층 연결
        return new OrderResponse(id, 1L, "홍길동", new BigDecimal("1200000"),
                               OrderStatus.CONFIRMED, List.of(), LocalDateTime.now());
    }
}