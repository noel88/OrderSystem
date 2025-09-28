package kr.minigate.ordersystem.application.service;

import kr.minigate.ordersystem.application.dto.PaymentQuery;

public interface PaymentQueryService {
    PaymentQuery getPaymentByOrderId(Long orderId);
    PaymentQuery getPayment(Long id);
}