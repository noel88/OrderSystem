package kr.minigate.ordersystem.application.service;

import kr.minigate.ordersystem.application.dto.OrderQuery;

import java.util.List;

public interface OrderQueryService {
    List<OrderQuery> getOrdersByMemberId(Long memberId);
    OrderQuery getOrder(Long id);
}