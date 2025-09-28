package kr.minigate.ordersystem.application.service;

import kr.minigate.ordersystem.application.dto.OrderCreateCommand;
import kr.minigate.ordersystem.application.dto.OrderQuery;
import kr.minigate.ordersystem.domain.OrderStatus;

public interface OrderCommandService {
    OrderQuery createOrder(OrderCreateCommand command);
    OrderQuery updateOrderStatus(Long id, OrderStatus status);
    OrderQuery cancelOrder(Long id);
    void deleteOrder(Long id);
}