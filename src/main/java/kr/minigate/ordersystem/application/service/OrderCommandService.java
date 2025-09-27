package kr.minigate.ordersystem.application.service;

import kr.minigate.ordersystem.application.dto.OrderCreateCommand;
import kr.minigate.ordersystem.application.dto.OrderQuery;

public interface OrderCommandService {
    OrderQuery createOrder(OrderCreateCommand command);
}