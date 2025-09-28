package kr.minigate.ordersystem.application.service;

import kr.minigate.ordersystem.application.dto.PaymentQuery;
import kr.minigate.ordersystem.domain.*;
import kr.minigate.ordersystem.domain.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class PaymentQueryServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentQueryServiceImpl paymentQueryService;

    @Test
    void 주문별_결제조회_성공() {
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
        order = org.mockito.Mockito.spy(order);
        given(order.getId()).willReturn(orderId);

        Payment payment = Payment.builder()
            .order(order)
            .amount(new BigDecimal("1200000"))
            .paymentMethod(PaymentMethod.CARD)
            .transactionId("TXN_12345678")
            .build();
        payment = org.mockito.Mockito.spy(payment);
        given(payment.getId()).willReturn(1L);

        given(paymentRepository.findByOrderId(orderId)).willReturn(Optional.of(payment));

        // when
        PaymentQuery result = paymentQueryService.getPaymentByOrderId(orderId);

        // then
        assertThat(result.orderId()).isEqualTo(orderId);
        assertThat(result.amount()).isEqualByComparingTo(new BigDecimal("1200000"));
        assertThat(result.paymentMethod()).isEqualTo(PaymentMethod.CARD);
        assertThat(result.status()).isEqualTo(PaymentStatus.COMPLETED);

        then(paymentRepository).should().findByOrderId(orderId);
    }

    @Test
    void 주문별_결제조회_실패_결제정보_없음() {
        // given
        Long orderId = 999L;

        given(paymentRepository.findByOrderId(orderId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> paymentQueryService.getPaymentByOrderId(orderId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("해당 주문의 결제 정보가 없습니다");

        then(paymentRepository).should().findByOrderId(orderId);
    }

    @Test
    void 결제조회_성공() {
        // given
        Long paymentId = 1L;
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
        given(order.getId()).willReturn(1L);

        Payment payment = Payment.builder()
            .order(order)
            .amount(new BigDecimal("1200000"))
            .paymentMethod(PaymentMethod.CARD)
            .transactionId("TXN_87654321")
            .build();
        payment = org.mockito.Mockito.spy(payment);
        given(payment.getId()).willReturn(paymentId);

        given(paymentRepository.findById(paymentId)).willReturn(Optional.of(payment));

        // when
        PaymentQuery result = paymentQueryService.getPayment(paymentId);

        // then
        assertThat(result.amount()).isEqualByComparingTo(new BigDecimal("1200000"));
        assertThat(result.paymentMethod()).isEqualTo(PaymentMethod.CARD);
        assertThat(result.status()).isEqualTo(PaymentStatus.COMPLETED);

        then(paymentRepository).should().findById(paymentId);
    }

    @Test
    void 결제조회_실패_존재하지_않는_결제() {
        // given
        Long paymentId = 999L;

        given(paymentRepository.findById(paymentId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> paymentQueryService.getPayment(paymentId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("존재하지 않는 결제입니다");

        then(paymentRepository).should().findById(paymentId);
    }
}