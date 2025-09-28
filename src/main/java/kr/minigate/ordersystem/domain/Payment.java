package kr.minigate.ordersystem.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "payments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(unique = true)
    private String transactionId;

    @Builder
    public Payment(Order order, BigDecimal amount, PaymentMethod paymentMethod, String transactionId) {
        this.order = order;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.transactionId = transactionId;
        this.status = PaymentStatus.COMPLETED;
    }

    public void cancel() {
        if (this.status == PaymentStatus.CANCELLED || this.status == PaymentStatus.REFUNDED) {
            throw new IllegalStateException("이미 취소되거나 환불된 결제입니다");
        }
        this.status = PaymentStatus.CANCELLED;
    }

    public void refund(BigDecimal refundAmount) {
        if (this.status == PaymentStatus.CANCELLED || this.status == PaymentStatus.REFUNDED) {
            throw new IllegalStateException("이미 취소되거나 환불된 결제입니다");
        }
        if (refundAmount.compareTo(this.amount) > 0) {
            throw new IllegalArgumentException("환불 금액이 결제 금액을 초과할 수 없습니다");
        }
        this.status = PaymentStatus.REFUNDED;
        this.amount = this.amount.subtract(refundAmount);
    }

    public static String generateTransactionId() {
        return "TXN_" + java.util.UUID.randomUUID().toString().substring(0, 8);
    }
}