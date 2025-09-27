package kr.minigate.ordersystem.application.service;

import kr.minigate.ordersystem.application.dto.PaymentQuery;
import kr.minigate.ordersystem.domain.Payment;
import kr.minigate.ordersystem.domain.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PaymentQueryServiceImpl implements PaymentQueryService {

    private final PaymentRepository paymentRepository;

    public PaymentQueryServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public PaymentQuery getPaymentByOrderId(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
            .orElseThrow(() -> new IllegalArgumentException("해당 주문의 결제 정보가 없습니다"));
        return PaymentQuery.from(payment);
    }

    @Override
    public PaymentQuery getPayment(Long id) {
        Payment payment = paymentRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 결제입니다"));
        return PaymentQuery.from(payment);
    }
}