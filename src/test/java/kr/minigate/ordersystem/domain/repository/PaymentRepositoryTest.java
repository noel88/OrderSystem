package kr.minigate.ordersystem.domain.repository;

import kr.minigate.ordersystem.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.yml")
class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProductRepository productRepository;

    private Order testOrder;
    private Member testMember;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        // 테스트 회원 생성
        testMember = Member.builder()
                .name("테스트 회원")
                .email("test@example.com")
                .phone("010-1234-5678")
                .address("서울시 강남구")
                .build();
        testMember = memberRepository.save(testMember);

        // 테스트 상품 생성
        testProduct = Product.builder()
                .name("테스트 상품")
                .description("테스트 상품 설명")
                .price(new BigDecimal("50000"))
                .stock(10)
                .build();
        testProduct = productRepository.save(testProduct);

        // 테스트 주문 생성
        testOrder = Order.builder()
                .member(testMember)
                .totalAmount(new BigDecimal("50000"))
                .build();
        testOrder = orderRepository.save(testOrder);
    }

    @Test
    @DisplayName("결제 정보 저장 성공")
    void save_Payment_Success() {
        // given
        Payment payment = createPayment();

        // when
        Payment savedPayment = paymentRepository.save(payment);

        // then
        assertThat(savedPayment).isNotNull();
        assertThat(savedPayment.getId()).isNotNull();
        assertThat(savedPayment.getOrder()).isEqualTo(testOrder);
        assertThat(savedPayment.getAmount()).isEqualByComparingTo(new BigDecimal("50000"));
        assertThat(savedPayment.getPaymentMethod()).isEqualTo(PaymentMethod.CARD);
        assertThat(savedPayment.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
    }

    @Test
    @DisplayName("ID로 결제 정보 조회")
    void findById_ExistingPayment_ReturnsPayment() {
        // given
        Payment payment = createPayment();
        Payment savedPayment = paymentRepository.save(payment);

        // when
        Optional<Payment> foundPayment = paymentRepository.findById(savedPayment.getId());

        // then
        assertThat(foundPayment).isPresent();
        assertThat(foundPayment.get().getId()).isEqualTo(savedPayment.getId());
        assertThat(foundPayment.get().getTransactionId()).isNotNull();
    }

    @Test
    @DisplayName("주문 ID로 결제 정보 조회")
    void findByOrderId_ReturnsPayment() {
        // given
        Payment payment = createPayment();
        paymentRepository.save(payment);

        // when
        Optional<Payment> foundPayment = paymentRepository.findByOrderId(testOrder.getId());

        // then
        assertThat(foundPayment).isPresent();
        assertThat(foundPayment.get().getOrder().getId()).isEqualTo(testOrder.getId());
    }

    @Test
    @DisplayName("결제 정보의 거래 ID 확인")
    void verifyTransactionId_Success() {
        // given
        String transactionId = UUID.randomUUID().toString();
        Payment payment = Payment.builder()
                .order(testOrder)
                .amount(testOrder.getTotalAmount())
                .paymentMethod(PaymentMethod.CARD)
                .transactionId(transactionId)
                .build();
        Payment savedPayment = paymentRepository.save(payment);

        // when
        Optional<Payment> foundPayment = paymentRepository.findById(savedPayment.getId());

        // then
        assertThat(foundPayment).isPresent();
        assertThat(foundPayment.get().getTransactionId()).isEqualTo(transactionId);
    }

    @Test
    @DisplayName("결제 상태 확인")
    void verifyPaymentStatus_Success() {
        // given
        Payment payment = createPayment();
        Payment savedPayment = paymentRepository.save(payment);

        // when
        Optional<Payment> foundPayment = paymentRepository.findById(savedPayment.getId());

        // then
        assertThat(foundPayment).isPresent();
        assertThat(foundPayment.get().getStatus()).isEqualTo(PaymentStatus.COMPLETED);
    }

    @Test
    @DisplayName("결제 방법 확인")
    void verifyPaymentMethod_Success() {
        // given
        Payment payment = createPayment();
        Payment savedPayment = paymentRepository.save(payment);

        // when
        Optional<Payment> foundPayment = paymentRepository.findById(savedPayment.getId());

        // then
        assertThat(foundPayment).isPresent();
        assertThat(foundPayment.get().getPaymentMethod()).isEqualTo(PaymentMethod.CARD);
    }

    @Test
    @DisplayName("결제 정보 조회 및 검증")
    void findAndVerify_Payment_Success() {
        // given
        Payment payment = createPayment();
        Payment savedPayment = paymentRepository.save(payment);

        // when
        Optional<Payment> foundPayment = paymentRepository.findById(savedPayment.getId());

        // then
        assertThat(foundPayment).isPresent();
        assertThat(foundPayment.get().getOrder()).isEqualTo(testOrder);
        assertThat(foundPayment.get().getAmount()).isEqualByComparingTo(testOrder.getTotalAmount());
    }

    @Test
    @DisplayName("결제 정보 삭제")
    void delete_Payment_Success() {
        // given
        Payment payment = createPayment();
        Payment savedPayment = paymentRepository.save(payment);
        Long paymentId = savedPayment.getId();

        // when
        paymentRepository.deleteById(paymentId);

        // then
        Optional<Payment> deletedPayment = paymentRepository.findById(paymentId);
        assertThat(deletedPayment).isEmpty();
    }

    @Test
    @DisplayName("전체 결제 정보 조회")
    void findAll_ReturnsAllPayments() {
        // given
        Payment payment1 = createPayment();

        Order order2 = Order.builder()
                .member(testMember)
                .totalAmount(new BigDecimal("30000"))
                .build();
        order2 = orderRepository.save(order2);

        Payment payment2 = Payment.builder()
                .order(order2)
                .amount(order2.getTotalAmount())
                .paymentMethod(PaymentMethod.MOBILE_PAY)
                .transactionId(UUID.randomUUID().toString())
                .build();

        paymentRepository.save(payment1);
        paymentRepository.save(payment2);

        // when
        List<Payment> payments = paymentRepository.findAll();

        // then
        assertThat(payments).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    @DisplayName("결제 금액 확인")
    void verifyPaymentAmount_Success() {
        // given
        Payment payment = createPayment();
        Payment savedPayment = paymentRepository.save(payment);

        // when & then
        assertThat(savedPayment.getAmount()).isEqualByComparingTo(testOrder.getTotalAmount());
    }

    @Test
    @DisplayName("결제 완료 상태 확인")
    void checkPaymentCompletedStatus_Success() {
        // given
        Payment payment = createPayment();
        Payment savedPayment = paymentRepository.save(payment);

        // when
        Optional<Payment> foundPayment = paymentRepository.findById(savedPayment.getId());

        // then
        assertThat(foundPayment).isPresent();
        assertThat(foundPayment.get().getStatus()).isEqualTo(PaymentStatus.COMPLETED);
    }

    @Test
    @DisplayName("다양한 결제 방법으로 결제 생성")
    void createPaymentWithDifferentMethods_Success() {
        // given
        Order order2 = Order.builder()
                .member(testMember)
                .totalAmount(new BigDecimal("30000"))
                .build();
        order2 = orderRepository.save(order2);

        Payment bankPayment = Payment.builder()
                .order(order2)
                .amount(order2.getTotalAmount())
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .transactionId(UUID.randomUUID().toString())
                .build();

        // when
        Payment savedPayment = paymentRepository.save(bankPayment);

        // then
        assertThat(savedPayment.getPaymentMethod()).isEqualTo(PaymentMethod.BANK_TRANSFER);
        assertThat(savedPayment.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
    }

    private Payment createPayment() {
        return Payment.builder()
                .order(testOrder)
                .amount(testOrder.getTotalAmount())
                .paymentMethod(PaymentMethod.CARD)
                .transactionId(UUID.randomUUID().toString())
                .build();
    }
}