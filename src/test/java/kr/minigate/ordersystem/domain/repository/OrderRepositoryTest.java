package kr.minigate.ordersystem.domain.repository;

import kr.minigate.ordersystem.domain.Member;
import kr.minigate.ordersystem.domain.Order;
import kr.minigate.ordersystem.domain.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.yml")
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProductRepository productRepository;

    private Member testMember;
    private Product testProduct1;
    private Product testProduct2;

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
        testProduct1 = Product.builder()
                .name("상품1")
                .description("상품1 설명")
                .price(new BigDecimal("10000"))
                .stock(100)
                .build();
        testProduct1 = productRepository.save(testProduct1);

        testProduct2 = Product.builder()
                .name("상품2")
                .description("상품2 설명")
                .price(new BigDecimal("20000"))
                .stock(50)
                .build();
        testProduct2 = productRepository.save(testProduct2);
    }

    @Test
    @DisplayName("주문 저장 성공")
    void save_Order_Success() {
        // given
        Order order = createOrder();

        // when
        Order savedOrder = orderRepository.save(order);

        // then
        assertThat(savedOrder).isNotNull();
        assertThat(savedOrder.getId()).isNotNull();
        assertThat(savedOrder.getMember()).isEqualTo(testMember);
        assertThat(savedOrder.getTotalAmount()).isEqualByComparingTo(new BigDecimal("50000"));
    }

    @Test
    @DisplayName("ID로 주문 조회")
    void findById_ExistingOrder_ReturnsOrder() {
        // given
        Order order = createOrder();
        Order savedOrder = orderRepository.save(order);

        // when
        Optional<Order> foundOrder = orderRepository.findById(savedOrder.getId());

        // then
        assertThat(foundOrder).isPresent();
        assertThat(foundOrder.get().getId()).isEqualTo(savedOrder.getId());
        assertThat(foundOrder.get().getMember()).isEqualTo(testMember);
    }

    @Test
    @DisplayName("회원 ID로 주문 목록 조회")
    void findByMemberIdOrderByCreatedAtDesc_ReturnsOrderList() {
        // given
        Order order1 = createOrder();
        Order order2 = Order.builder()
                .member(testMember)
                .totalAmount(new BigDecimal("30000"))
                .build();

        orderRepository.save(order1);
        orderRepository.save(order2);

        // when
        List<Order> orders = orderRepository.findByMemberIdOrderByCreatedAtDesc(testMember.getId());

        // then
        assertThat(orders).hasSize(2);
        assertThat(orders).allMatch(order -> order.getMember().getId().equals(testMember.getId()));
    }

    @Test
    @DisplayName("주문 상태 확인")
    void verifyOrderStatus_Success() {
        // given - Orders are created with CONFIRMED status by default
        Order order = createOrder();
        Order savedOrder = orderRepository.save(order);

        // when
        Optional<Order> foundOrder = orderRepository.findById(savedOrder.getId());

        // then
        assertThat(foundOrder).isPresent();
        assertThat(foundOrder.get().getStatus()).isNotNull();
        assertThat(foundOrder.get().getMember()).isEqualTo(testMember);
    }

    @Test
    @DisplayName("주문 조회 및 상태 확인")
    void findOrder_CheckStatus_Success() {
        // given
        Order order = createOrder();
        Order savedOrder = orderRepository.save(order);

        // when
        Optional<Order> foundOrder = orderRepository.findById(savedOrder.getId());

        // then
        assertThat(foundOrder).isPresent();
        assertThat(foundOrder.get().getStatus()).isNotNull();
    }

    @Test
    @DisplayName("주문 삭제")
    void delete_Order_Success() {
        // given
        Order order = createOrder();
        Order savedOrder = orderRepository.save(order);
        Long orderId = savedOrder.getId();

        // when
        orderRepository.deleteById(orderId);

        // then
        Optional<Order> deletedOrder = orderRepository.findById(orderId);
        assertThat(deletedOrder).isEmpty();
    }

    @Test
    @DisplayName("전체 주문 조회")
    void findAll_ReturnsAllOrders() {
        // given
        Order order1 = createOrder();
        Order order2 = createOrder();
        orderRepository.save(order1);
        orderRepository.save(order2);

        // when
        List<Order> orders = orderRepository.findAll();

        // then
        assertThat(orders).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    @DisplayName("주문 총 금액 계산")
    void calculateTotalAmount_Success() {
        // given
        Order order = createOrder();

        // when
        Order savedOrder = orderRepository.save(order);

        // then
        assertThat(savedOrder.getTotalAmount()).isEqualByComparingTo(new BigDecimal("50000"));
    }

    @Test
    @DisplayName("주문 저장 후 조회")
    void saveAndFind_Order_Success() {
        // given
        Order order = createOrder();
        Order savedOrder = orderRepository.save(order);

        // when
        Optional<Order> foundOrder = orderRepository.findById(savedOrder.getId());

        // then
        assertThat(foundOrder).isPresent();
        assertThat(foundOrder.get().getTotalAmount()).isEqualByComparingTo(savedOrder.getTotalAmount());
    }

    @Test
    @DisplayName("다른 총액으로 주문 저장")
    void saveOrderWithDifferentAmount_Success() {
        // given
        Order order = Order.builder()
                .member(testMember)
                .totalAmount(new BigDecimal("30000"))
                .build();

        // when
        Order savedOrder = orderRepository.save(order);

        // then
        assertThat(savedOrder.getId()).isNotNull();
        assertThat(savedOrder.getTotalAmount()).isEqualByComparingTo(new BigDecimal("30000"));
        assertThat(savedOrder.getMember()).isEqualTo(testMember);
    }

    private Order createOrder() {
        return Order.builder()
                .member(testMember)
                .totalAmount(new BigDecimal("50000"))
                .build();
    }
}