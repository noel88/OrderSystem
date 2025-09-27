package kr.minigate.ordersystem.application.service;

import kr.minigate.ordersystem.application.dto.PaymentCreateCommand;
import kr.minigate.ordersystem.application.dto.PaymentQuery;
import kr.minigate.ordersystem.domain.Order;
import kr.minigate.ordersystem.domain.Payment;
import kr.minigate.ordersystem.domain.repository.OrderRepository;
import kr.minigate.ordersystem.domain.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

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

        String transactionId = "TXN_" + UUID.randomUUID().toString().substring(0, 8);

        Payment payment = Payment.builder()
            .order(order)
            .amount(order.getTotalAmount())
            .paymentMethod(command.paymentMethod())
            .transactionId(transactionId)
            .build();

        Payment savedPayment = paymentRepository.save(payment);
        return PaymentQuery.from(savedPayment);
    }
}