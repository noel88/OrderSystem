package kr.minigate.ordersystem.application.dto;

import kr.minigate.ordersystem.domain.PaymentMethod;

public record PaymentCreateCommand(
    Long orderId,
    PaymentMethod paymentMethod
) {
}