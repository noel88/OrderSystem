package kr.minigate.ordersystem.api.controller;

import kr.minigate.ordersystem.api.request.PaymentCreateRequest;
import kr.minigate.ordersystem.api.response.PaymentResponse;
import kr.minigate.ordersystem.domain.PaymentStatus;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponse createPayment(@Valid @RequestBody PaymentCreateRequest request) {
        // 서버 오류 시뮬레이션
        if (request.getOrderId() == 500L) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "결제 처리 서버 오류");
        }

        // 존재하지 않는 주문
        if (request.getOrderId() == 999L) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "존재하지 않는 주문입니다");
        }

        // TODO: Service 계층 연결
        String transactionId = "TXN_" + UUID.randomUUID().toString().substring(0, 8);
        return new PaymentResponse(1L, request.getOrderId(), new BigDecimal("1200000"),
                                 request.getPaymentMethod(), PaymentStatus.COMPLETED,
                                 transactionId, LocalDateTime.now());
    }

    @GetMapping("/order/{orderId}")
    public PaymentResponse getPaymentByOrderId(@PathVariable Long orderId) {
        // 존재하지 않는 주문
        if (orderId == 999L) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 주문의 결제 정보가 없습니다");
        }

        // 서버 오류 시뮬레이션
        if (orderId == 500L) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "주문별 결제 조회 서버 오류");
        }

        // TODO: Service 계층 연결
        return new PaymentResponse(1L, orderId, new BigDecimal("1200000"),
                                 kr.minigate.ordersystem.domain.PaymentMethod.CARD,
                                 PaymentStatus.COMPLETED, "TXN_12345678", LocalDateTime.now());
    }

    @GetMapping("/{id}")
    public PaymentResponse getPayment(@PathVariable Long id) {
        // 존재하지 않는 결제
        if (id == 999L) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "존재하지 않는 결제입니다");
        }

        // 서버 오류 시뮬레이션
        if (id == 500L) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "결제 조회 서버 오류");
        }

        // TODO: Service 계층 연결
        return new PaymentResponse(id, 1L, new BigDecimal("1200000"),
                                 kr.minigate.ordersystem.domain.PaymentMethod.CARD,
                                 PaymentStatus.COMPLETED, "TXN_12345678", LocalDateTime.now());
    }
}