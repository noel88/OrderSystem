package kr.minigate.ordersystem.api.response;

import kr.minigate.ordersystem.application.dto.OrderQuery;
import kr.minigate.ordersystem.domain.Order;
import kr.minigate.ordersystem.domain.OrderStatus;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class OrderResponse {

    private final Long id;
    private final Long memberId;
    private final String memberName;
    private final BigDecimal totalAmount;
    private final OrderStatus status;
    private final List<OrderItemResponse> orderItems;
    private final LocalDateTime createdAt;

    public OrderResponse(Order order) {
        this.id = order.getId();
        this.memberId = order.getMember().getId();
        this.memberName = order.getMember().getName();
        this.totalAmount = order.getTotalAmount();
        this.status = order.getStatus();
        this.orderItems = List.of(); // TODO: OrderItem 변환
        this.createdAt = order.getCreatedAt();
    }

    public OrderResponse(OrderQuery orderQuery) {
        this.id = orderQuery.id();
        this.memberId = orderQuery.memberId();
        this.memberName = orderQuery.memberName();
        this.totalAmount = orderQuery.totalAmount();
        this.status = orderQuery.status();
        this.orderItems = orderQuery.orderItems().stream()
            .map(item -> new OrderItemResponse(
                item.productId(),
                item.productName(),
                item.quantity(),
                item.price(),
                item.amount()
            ))
            .collect(Collectors.toList());
        this.createdAt = orderQuery.createdAt();
    }

    public OrderResponse(Long id, Long memberId, String memberName, BigDecimal totalAmount,
                        OrderStatus status, List<OrderItemResponse> orderItems, LocalDateTime createdAt) {
        this.id = id;
        this.memberId = memberId;
        this.memberName = memberName;
        this.totalAmount = totalAmount;
        this.status = status;
        this.orderItems = orderItems;
        this.createdAt = createdAt;
    }


    @Getter
    public static class OrderItemResponse {
        private final Long productId;
        private final String productName;
        private final Integer quantity;
        private final BigDecimal price;
        private final BigDecimal amount;

        public OrderItemResponse(Long productId, String productName, Integer quantity,
                               BigDecimal price, BigDecimal amount) {
            this.productId = productId;
            this.productName = productName;
            this.quantity = quantity;
            this.price = price;
            this.amount = amount;
        }
    }
}