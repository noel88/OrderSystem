package kr.minigate.ordersystem.api.controller;

import kr.minigate.ordersystem.api.request.PaymentCreateRequest;
import kr.minigate.ordersystem.api.response.PaymentResponse;
import kr.minigate.ordersystem.application.dto.PaymentCreateCommand;
import kr.minigate.ordersystem.application.dto.PaymentQuery;
import kr.minigate.ordersystem.application.service.PaymentCommandService;
import kr.minigate.ordersystem.application.service.PaymentQueryService;
import kr.minigate.ordersystem.domain.PaymentStatus;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentCommandService paymentCommandService;
    private final PaymentQueryService paymentQueryService;

    public PaymentController(PaymentCommandService paymentCommandService, PaymentQueryService paymentQueryService) {
        this.paymentCommandService = paymentCommandService;
        this.paymentQueryService = paymentQueryService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponse createPayment(@Valid @RequestBody PaymentCreateRequest request) {
        // 서버 오류 시뮬레이션
        if (request.getOrderId() == 500L) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "결제 처리 서버 오류");
        }

        try {
            PaymentCreateCommand command = new PaymentCreateCommand(
                request.getOrderId(),
                request.getPaymentMethod()
            );
            PaymentQuery paymentQuery = paymentCommandService.processPayment(command);
            return new PaymentResponse(paymentQuery);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/order/{orderId}")
    public PaymentResponse getPaymentByOrderId(@PathVariable Long orderId) {
        // 서버 오류 시뮬레이션
        if (orderId == 500L) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "주문별 결제 조회 서버 오류");
        }

        try {
            PaymentQuery paymentQuery = paymentQueryService.getPaymentByOrderId(orderId);
            return new PaymentResponse(paymentQuery);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public PaymentResponse getPayment(@PathVariable Long id) {
        // 서버 오류 시뮬레이션
        if (id == 500L) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "결제 조회 서버 오류");
        }

        try {
            PaymentQuery paymentQuery = paymentQueryService.getPayment(id);
            return new PaymentResponse(paymentQuery);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping
    public List<PaymentResponse> getAllPayments() {
        List<PaymentQuery> paymentQueries = paymentQueryService.getAllPayments();
        return paymentQueries.stream()
            .map(PaymentResponse::new)
            .collect(Collectors.toList());
    }

    @PatchMapping("/{id}/cancel")
    public PaymentResponse cancelPayment(@PathVariable Long id) {
        try {
            PaymentQuery paymentQuery = paymentCommandService.cancelPayment(id);
            return new PaymentResponse(paymentQuery);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PatchMapping("/{id}/refund")
    public PaymentResponse refundPayment(@PathVariable Long id, @RequestParam BigDecimal refundAmount) {
        try {
            PaymentQuery paymentQuery = paymentCommandService.refundPayment(id, refundAmount);
            return new PaymentResponse(paymentQuery);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePayment(@PathVariable Long id) {
        try {
            paymentCommandService.deletePayment(id);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}