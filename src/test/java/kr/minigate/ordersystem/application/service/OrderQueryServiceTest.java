package kr.minigate.ordersystem.application.service;

import kr.minigate.ordersystem.application.dto.OrderQuery;
import kr.minigate.ordersystem.domain.Member;
import kr.minigate.ordersystem.domain.Order;
import kr.minigate.ordersystem.domain.OrderStatus;
import kr.minigate.ordersystem.domain.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class OrderQueryServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderQueryServiceImpl orderQueryService;

    @Test
    void 회원별_주문목록_조회_성공() {
        // given
        Long memberId = 1L;
        Member member = Member.builder()
            .name("홍길동")
            .email("hong@test.com")
            .phone("010-1234-5678")
            .address("서울시 강남구")
            .build();

        List<Order> orders = List.of(
            Order.builder()
                .member(member)
                .totalAmount(new BigDecimal("1200000"))
                .build(),
            Order.builder()
                .member(member)
                .totalAmount(new BigDecimal("800000"))
                .build()
        );

        given(orderRepository.findByMemberIdOrderByCreatedAtDesc(memberId)).willReturn(orders);

        // when
        List<OrderQuery> result = orderQueryService.getOrdersByMemberId(memberId);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).memberName()).isEqualTo("홍길동");
        assertThat(result.get(0).totalAmount()).isEqualByComparingTo(new BigDecimal("1200000"));
        assertThat(result.get(1).totalAmount()).isEqualByComparingTo(new BigDecimal("800000"));

        then(orderRepository).should().findByMemberIdOrderByCreatedAtDesc(memberId);
    }

    @Test
    void 주문_조회_성공() {
        // given
        Long orderId = 1L;
        Member member = Member.builder()
            .name("홍길동")
            .email("hong@test.com")
            .phone("010-1234-5678")
            .address("서울시 강남구")
            .build();

        Order order = Order.builder()
            .member(member)
            .totalAmount(new BigDecimal("1200000"))
            .build();

        given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

        // when
        OrderQuery result = orderQueryService.getOrder(orderId);

        // then
        assertThat(result.memberName()).isEqualTo("홍길동");
        assertThat(result.totalAmount()).isEqualByComparingTo(new BigDecimal("1200000"));
        assertThat(result.status()).isEqualTo(OrderStatus.CONFIRMED);

        then(orderRepository).should().findById(orderId);
    }

    @Test
    void 주문_조회_실패_존재하지_않는_주문() {
        // given
        Long orderId = 999L;

        given(orderRepository.findById(orderId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> orderQueryService.getOrder(orderId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("존재하지 않는 주문입니다");

        then(orderRepository).should().findById(orderId);
    }
}