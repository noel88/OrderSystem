package kr.minigate.ordersystem.application.service;

import kr.minigate.ordersystem.application.dto.PaymentCreateCommand;
import kr.minigate.ordersystem.application.dto.PaymentQuery;

import java.math.BigDecimal;

public interface PaymentCommandService {
    PaymentQuery processPayment(PaymentCreateCommand command);
    PaymentQuery cancelPayment(Long id);
    PaymentQuery refundPayment(Long id, BigDecimal refundAmount);
    void deletePayment(Long id);
}