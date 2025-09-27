package kr.minigate.ordersystem.api.response;

import kr.minigate.ordersystem.domain.Payment;
import kr.minigate.ordersystem.domain.PaymentMethod;
import kr.minigate.ordersystem.domain.PaymentStatus;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class PaymentResponse {

    private final Long id;
    private final Long orderId;
    private final BigDecimal amount;
    private final PaymentMethod paymentMethod;
    private final PaymentStatus status;
    private final String transactionId;
    private final LocalDateTime createdAt;

    public PaymentResponse(Payment payment) {
        this.id = payment.getId();
        this.orderId = payment.getOrder().getId();
        this.amount = payment.getAmount();
        this.paymentMethod = payment.getPaymentMethod();
        this.status = payment.getStatus();
        this.transactionId = payment.getTransactionId();
        this.createdAt = payment.getCreatedAt();
    }

    public PaymentResponse(Long id, Long orderId, BigDecimal amount, PaymentMethod paymentMethod,
                          PaymentStatus status, String transactionId, LocalDateTime createdAt) {
        this.id = id;
        this.orderId = orderId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.transactionId = transactionId;
        this.createdAt = createdAt;
    }

}