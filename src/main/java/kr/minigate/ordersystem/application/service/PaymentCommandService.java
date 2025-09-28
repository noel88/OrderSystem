package kr.minigate.ordersystem.application.service;

import kr.minigate.ordersystem.application.dto.PaymentCreateCommand;
import kr.minigate.ordersystem.application.dto.PaymentQuery;

public interface PaymentCommandService {
    PaymentQuery processPayment(PaymentCreateCommand command);
}