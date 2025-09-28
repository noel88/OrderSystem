package kr.minigate.ordersystem.application.service;

import kr.minigate.ordersystem.application.dto.OrderCreateCommand;
import kr.minigate.ordersystem.application.dto.OrderQuery;
import kr.minigate.ordersystem.domain.Member;
import kr.minigate.ordersystem.domain.Order;
import kr.minigate.ordersystem.domain.OrderItem;
import kr.minigate.ordersystem.domain.OrderStatus;
import kr.minigate.ordersystem.domain.Product;
import kr.minigate.ordersystem.domain.repository.MemberRepository;
import kr.minigate.ordersystem.domain.repository.OrderRepository;
import kr.minigate.ordersystem.domain.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class OrderCommandServiceImpl implements OrderCommandService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    public OrderCommandServiceImpl(OrderRepository orderRepository,
                                 MemberRepository memberRepository,
                                 ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.memberRepository = memberRepository;
        this.productRepository = productRepository;
    }

    @Override
    public OrderQuery createOrder(OrderCreateCommand command) {
        // 회원 존재 여부 확인
        Member member = memberRepository.findById(command.memberId())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다"));

        // 주문 아이템 생성 및 검증
        List<OrderItem> orderItems = command.orderItems().stream()
            .map(itemCommand -> {
                Product product = productRepository.findById(itemCommand.productId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다"));

                // 재고 확인
                if (product.getStock() < itemCommand.quantity()) {
                    throw new IllegalArgumentException("재고가 부족합니다. 상품: " + product.getName());
                }

                // 재고 차감
                product.decreaseStock(itemCommand.quantity());
                productRepository.save(product);

                // 주문 아이템 생성
                BigDecimal itemAmount = product.getPrice().multiply(new BigDecimal(itemCommand.quantity()));

                return OrderItem.builder()
                    .product(product)
                    .quantity(itemCommand.quantity())
                    .price(product.getPrice())
                    .amount(itemAmount)
                    .build();
            })
            .collect(java.util.stream.Collectors.toList());

        // 도메인 팩토리 메서드 사용
        Order order = Order.createOrder(member, orderItems);

        Order savedOrder = orderRepository.save(order);
        return OrderQuery.from(savedOrder);
    }

    @Override
    public OrderQuery updateOrderStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다"));

        order.updateStatus(status);
        return OrderQuery.from(order);
    }

    @Override
    public OrderQuery cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다"));

        order.cancel();
        return OrderQuery.from(order);
    }

    @Override
    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다"));

        orderRepository.delete(order);
    }
}