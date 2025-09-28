package kr.minigate.ordersystem.application.service;

import kr.minigate.ordersystem.application.dto.PaymentCreateCommand;
import kr.minigate.ordersystem.application.dto.PaymentQuery;
import kr.minigate.ordersystem.domain.Order;
import kr.minigate.ordersystem.domain.OrderStatus;
import kr.minigate.ordersystem.domain.Payment;
import kr.minigate.ordersystem.domain.PaymentStatus;
import kr.minigate.ordersystem.domain.Product;
import kr.minigate.ordersystem.domain.repository.OrderRepository;
import kr.minigate.ordersystem.domain.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional
public class PaymentCommandServiceImpl implements PaymentCommandService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public PaymentCommandServiceImpl(PaymentRepository paymentRepository,
                                   OrderRepository orderRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public PaymentQuery processPayment(PaymentCreateCommand command) {
        Order order = orderRepository.findById(command.orderId())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다"));

        // 중복 결제 방지
        if (paymentRepository.findByOrderId(order.getId()).isPresent()) {
            throw new IllegalArgumentException("이미 결제된 주문입니다");
        }

        String transactionId = Payment.generateTransactionId();

        Payment payment = Payment.builder()
            .order(order)
            .amount(order.getTotalAmount())
            .paymentMethod(command.paymentMethod())
            .transactionId(transactionId)
            .build();

        Payment savedPayment = paymentRepository.save(payment);
        return PaymentQuery.from(savedPayment);
    }

    @Override
    public PaymentQuery cancelPayment(Long id) {
        Payment payment = paymentRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 결제입니다"));

        // 주문 상태 확인
        Order order = payment.getOrder();
        if (!order.canCancelPayment()) {
            throw new IllegalStateException("배송 중이거나 배송 완료된 주문의 결제는 취소할 수 없습니다");
        }

        payment.cancel();

        // 재고 복원
        order.restoreStock();

        return PaymentQuery.from(payment);
    }

    @Override
    public PaymentQuery refundPayment(Long id, BigDecimal refundAmount) {
        Payment payment = paymentRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 결제입니다"));

        // 환불 가능 상태 확인
        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new IllegalStateException("완료된 결제만 환불 가능합니다");
        }

        // 환불 금액 검증
        if (refundAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("환불 금액은 0보다 커야 합니다");
        }

        payment.refund(refundAmount);
        return PaymentQuery.from(payment);
    }

    @Override
    public void deletePayment(Long id) {
        Payment payment = paymentRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 결제입니다"));

        paymentRepository.delete(payment);
    }
}