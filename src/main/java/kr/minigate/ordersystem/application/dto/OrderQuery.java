package kr.minigate.ordersystem.application.dto;

import kr.minigate.ordersystem.domain.Order;
import kr.minigate.ordersystem.domain.OrderItem;
import kr.minigate.ordersystem.domain.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderQuery(
    Long id,
    Long memberId,
    String memberName,
    BigDecimal totalAmount,
    OrderStatus status,
    List<OrderItemQuery> orderItems,
    LocalDateTime createdAt
) {
    public static OrderQuery from(Order order) {
        List<OrderItemQuery> orderItemQueries = order.getOrderItems() != null ?
            order.getOrderItems().stream()
                .map(OrderItemQuery::from)
                .toList() : List.of();

        return new OrderQuery(
            order.getId(),
            order.getMember().getId(),
            order.getMember().getName(),
            order.getTotalAmount(),
            order.getStatus(),
            orderItemQueries,
            order.getCreatedAt()
        );
    }

    public record OrderItemQuery(
        Long id,
        Long productId,
        String productName,
        Integer quantity,
        BigDecimal price,
        BigDecimal amount
    ) {
        public static OrderItemQuery from(OrderItem orderItem) {
            return new OrderItemQuery(
                orderItem.getId(),
                orderItem.getProduct().getId(),
                orderItem.getProduct().getName(),
                orderItem.getQuantity(),
                orderItem.getPrice(),
                orderItem.getAmount()
            );
        }
    }
}