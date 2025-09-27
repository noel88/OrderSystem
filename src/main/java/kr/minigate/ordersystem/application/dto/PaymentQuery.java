package kr.minigate.ordersystem.application.dto;

import kr.minigate.ordersystem.domain.Payment;
import kr.minigate.ordersystem.domain.PaymentMethod;
import kr.minigate.ordersystem.domain.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentQuery(
    Long id,
    Long orderId,
    BigDecimal amount,
    PaymentMethod paymentMethod,
    PaymentStatus status,
    String transactionId,
    LocalDateTime createdAt
) {
    public static PaymentQuery from(Payment payment) {
        return new PaymentQuery(
            payment.getId(),
            payment.getOrder().getId(),
            payment.getAmount(),
            payment.getPaymentMethod(),
            payment.getStatus(),
            payment.getTransactionId(),
            payment.getCreatedAt()
        );
    }
}