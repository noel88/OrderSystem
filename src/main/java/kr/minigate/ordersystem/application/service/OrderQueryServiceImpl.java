package kr.minigate.ordersystem.application.service;

import kr.minigate.ordersystem.application.dto.OrderQuery;
import kr.minigate.ordersystem.domain.Order;
import kr.minigate.ordersystem.domain.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class OrderQueryServiceImpl implements OrderQueryService {

    private final OrderRepository orderRepository;

    public OrderQueryServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public List<OrderQuery> getOrdersByMemberId(Long memberId) {
        return orderRepository.findByMemberIdOrderByCreatedAtDesc(memberId)
            .stream()
            .map(OrderQuery::from)
            .toList();
    }

    @Override
    public OrderQuery getOrder(Long id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다"));
        return OrderQuery.from(order);
    }

    @Override
    public List<OrderQuery> getAllOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc()
            .stream()
            .map(OrderQuery::from)
            .toList();
    }
}