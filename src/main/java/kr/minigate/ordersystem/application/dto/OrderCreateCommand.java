package kr.minigate.ordersystem.application.dto;

import java.util.List;

public record OrderCreateCommand(
    Long memberId,
    List<OrderItemCommand> orderItems
) {
    public record OrderItemCommand(
        Long productId,
        Integer quantity
    ) {
    }
}