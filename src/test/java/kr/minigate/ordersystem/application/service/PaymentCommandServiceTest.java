package kr.minigate.ordersystem.application.service;

import kr.minigate.ordersystem.application.dto.PaymentCreateCommand;
import kr.minigate.ordersystem.application.dto.PaymentQuery;
import kr.minigate.ordersystem.domain.*;
import kr.minigate.ordersystem.domain.repository.OrderRepository;
import kr.minigate.ordersystem.domain.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class PaymentCommandServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private PaymentCommandServiceImpl paymentCommandService;

    @Test
    void 결제처리_성공() {
        // given
        Long orderId = 1L;
        PaymentCreateCommand command = new PaymentCreateCommand(orderId, PaymentMethod.CARD);

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
        order = org.mockito.Mockito.spy(order);
        given(order.getId()).willReturn(orderId);

        Payment savedPayment = Payment.builder()
            .order(order)
            .amount(new BigDecimal("1200000"))
            .paymentMethod(PaymentMethod.CARD)
            .transactionId("TXN_12345678")
            .build();
        savedPayment = org.mockito.Mockito.spy(savedPayment);
        given(savedPayment.getId()).willReturn(1L);

        given(orderRepository.findById(orderId)).willReturn(Optional.of(order));
        given(paymentRepository.save(any(Payment.class))).willReturn(savedPayment);

        // when
        PaymentQuery result = paymentCommandService.processPayment(command);

        // then
        assertThat(result.orderId()).isEqualTo(orderId);
        assertThat(result.amount()).isEqualByComparingTo(new BigDecimal("1200000"));
        assertThat(result.paymentMethod()).isEqualTo(PaymentMethod.CARD);
        assertThat(result.status()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(result.transactionId()).startsWith("TXN_");

        then(orderRepository).should().findById(orderId);
        then(paymentRepository).should().save(any(Payment.class));
    }

    @Test
    void 결제처리_실패_존재하지_않는_주문() {
        // given
        Long orderId = 999L;
        PaymentCreateCommand command = new PaymentCreateCommand(orderId, PaymentMethod.CARD);

        given(orderRepository.findById(orderId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> paymentCommandService.processPayment(command))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("존재하지 않는 주문입니다");

        then(orderRepository).should().findById(orderId);
        then(paymentRepository).shouldHaveNoInteractions();
    }
}