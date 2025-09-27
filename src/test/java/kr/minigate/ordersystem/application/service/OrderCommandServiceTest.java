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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class OrderCommandServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderCommandServiceImpl orderCommandService;

    @Test
    void 주문생성_성공() {
        // given
        Long memberId = 1L;
        Long productId = 1L;
        Integer quantity = 2;

        Member member = Member.builder()
            .name("홍길동")
            .email("hong@test.com")
            .phone("010-1234-5678")
            .address("서울시 강남구")
            .build();

        Product product = Product.builder()
            .name("아이폰 15")
            .description("애플 스마트폰")
            .price(new BigDecimal("1200000"))
            .stock(10)
            .build();

        OrderCreateCommand command = new OrderCreateCommand(
            memberId,
            List.of(new OrderCreateCommand.OrderItemCommand(productId, quantity))
        );

        // Mock member with ID
        Member memberWithId = Member.builder()
            .name("홍길동")
            .email("hong@test.com")
            .phone("010-1234-5678")
            .address("서울시 강남구")
            .build();
        memberWithId = org.mockito.Mockito.spy(memberWithId);
        given(memberWithId.getId()).willReturn(memberId);

        Order savedOrder = Order.builder()
            .member(memberWithId)
            .totalAmount(new BigDecimal("2400000"))
            .build();
        savedOrder = org.mockito.Mockito.spy(savedOrder);
        given(savedOrder.getId()).willReturn(1L);

        given(memberRepository.findById(memberId)).willReturn(Optional.of(memberWithId));
        given(productRepository.findById(productId)).willReturn(Optional.of(product));
        given(orderRepository.save(any(Order.class))).willReturn(savedOrder);

        // when
        OrderQuery result = orderCommandService.createOrder(command);

        // then
        assertThat(result.memberId()).isEqualTo(memberId);
        assertThat(result.memberName()).isEqualTo("홍길동");
        assertThat(result.totalAmount()).isEqualByComparingTo(new BigDecimal("2400000"));

        then(memberRepository).should().findById(memberId);
        then(productRepository).should().findById(productId);
        then(orderRepository).should().save(any(Order.class));
    }

    @Test
    void 주문생성_실패_존재하지_않는_회원() {
        // given
        Long memberId = 999L;
        OrderCreateCommand command = new OrderCreateCommand(
            memberId,
            List.of(new OrderCreateCommand.OrderItemCommand(1L, 2))
        );

        given(memberRepository.findById(memberId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> orderCommandService.createOrder(command))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("존재하지 않는 회원입니다");

        then(memberRepository).should().findById(memberId);
        then(productRepository).shouldHaveNoInteractions();
        then(orderRepository).shouldHaveNoInteractions();
    }

    @Test
    void 주문생성_실패_존재하지_않는_상품() {
        // given
        Long memberId = 1L;
        Long productId = 999L;

        Member member = Member.builder()
            .name("홍길동")
            .email("hong@test.com")
            .phone("010-1234-5678")
            .address("서울시 강남구")
            .build();

        OrderCreateCommand command = new OrderCreateCommand(
            memberId,
            List.of(new OrderCreateCommand.OrderItemCommand(productId, 2))
        );

        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(productRepository.findById(productId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> orderCommandService.createOrder(command))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("존재하지 않는 상품입니다");

        then(memberRepository).should().findById(memberId);
        then(productRepository).should().findById(productId);
        then(orderRepository).shouldHaveNoInteractions();
    }
}