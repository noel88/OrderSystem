package kr.minigate.ordersystem.application.service;

import kr.minigate.ordersystem.application.dto.OrderCreateCommand;
import kr.minigate.ordersystem.application.dto.OrderQuery;
import kr.minigate.ordersystem.domain.Member;
import kr.minigate.ordersystem.domain.Order;
import kr.minigate.ordersystem.domain.OrderItem;
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
        Member member = memberRepository.findById(command.memberId())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다"));

        List<OrderItem> orderItems = command.orderItems().stream()
            .map(itemCommand -> {
                Product product = productRepository.findById(itemCommand.productId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다"));

                return OrderItem.builder()
                    .product(product)
                    .quantity(itemCommand.quantity())
                    .price(product.getPrice())
                    .build();
            })
            .collect(java.util.stream.Collectors.toList());

        BigDecimal totalAmount = orderItems.stream()
            .map(OrderItem::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = Order.builder()
            .member(member)
            .totalAmount(totalAmount)
            .build();

        Order savedOrder = orderRepository.save(order);
        return OrderQuery.from(savedOrder);
    }
}