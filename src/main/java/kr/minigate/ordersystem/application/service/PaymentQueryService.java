package kr.minigate.ordersystem.application.service;

import kr.minigate.ordersystem.application.dto.PaymentQuery;

import java.util.List;

public interface PaymentQueryService {
    PaymentQuery getPaymentByOrderId(Long orderId);
    PaymentQuery getPayment(Long id);
    List<PaymentQuery> getAllPayments();
}