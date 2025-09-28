package kr.minigate.ordersystem.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.minigate.ordersystem.api.request.OrderCreateRequest;
import kr.minigate.ordersystem.domain.Member;
import kr.minigate.ordersystem.domain.Order;
import kr.minigate.ordersystem.domain.Product;
import kr.minigate.ordersystem.domain.repository.MemberRepository;
import kr.minigate.ordersystem.domain.repository.OrderRepository;
import kr.minigate.ordersystem.domain.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(locations = "classpath:application-test.yml")
class OrderApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
        orderRepository.deleteAll();
        memberRepository.deleteAll();
        productRepository.deleteAll();

        // 테스트 회원 생성
        testMember = Member.builder()
                .name("테스트회원")
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
    @DisplayName("주문 생성 성공")
    void createOrder_Success() throws Exception {
        // given
        OrderCreateRequest.OrderItemRequest item1 = new OrderCreateRequest.OrderItemRequest(testProduct1.getId(), 2);

        OrderCreateRequest.OrderItemRequest item2 = new OrderCreateRequest.OrderItemRequest(testProduct2.getId(), 1);

        OrderCreateRequest request = new OrderCreateRequest(testMember.getId(), Arrays.asList(item1, item2));

        // when & then
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.memberId").value(testMember.getId()))
                .andExpect(jsonPath("$.totalAmount").value(40000))
                .andExpect(jsonPath("$.status").value("CONFIRMED"))
                .andExpect(jsonPath("$.orderItems", hasSize(2)))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @DisplayName("주문 생성 실패 - 회원 없음")
    void createOrder_Fail_MemberNotFound() throws Exception {
        // given
        OrderCreateRequest.OrderItemRequest item = new OrderCreateRequest.OrderItemRequest(testProduct1.getId(), 1);

        OrderCreateRequest request = new OrderCreateRequest(999999L, Arrays.asList(item));

        // when & then
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("주문 생성 실패 - 상품 없음")
    void createOrder_Fail_ProductNotFound() throws Exception {
        // given
        OrderCreateRequest.OrderItemRequest item = new OrderCreateRequest.OrderItemRequest(999999L, 1);

        OrderCreateRequest request = new OrderCreateRequest(testMember.getId(), Arrays.asList(item));

        // when & then
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("주문 생성 실패 - 재고 부족")
    void createOrder_Fail_InsufficientStock() throws Exception {
        // given
        OrderCreateRequest.OrderItemRequest item = new OrderCreateRequest.OrderItemRequest(testProduct1.getId(), 1000); // 재고보다 많은 수량

        OrderCreateRequest request = new OrderCreateRequest(testMember.getId(), Arrays.asList(item));

        // when & then
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("주문 조회 성공")
    void getOrder_Success() throws Exception {
        // given
        Order order = Order.builder()
                .member(testMember)
                .totalAmount(new BigDecimal("30000"))
                .build();
        Order savedOrder = orderRepository.save(order);

        // when & then
        mockMvc.perform(get("/api/orders/{id}", savedOrder.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedOrder.getId()))
                .andExpect(jsonPath("$.memberId").value(testMember.getId()))
                .andExpect(jsonPath("$.totalAmount").value(30000))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    @DisplayName("주문 조회 실패 - 존재하지 않는 ID")
    void getOrder_Fail_NotFound() throws Exception {
        // when & then
        mockMvc.perform(get("/api/orders/{id}", 999999L))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("회원별 주문 목록 조회 성공")
    void getOrdersByMember_Success() throws Exception {
        // given
        Order order1 = Order.builder()
                .member(testMember)
                .totalAmount(new BigDecimal("10000"))
                .build();

        Order order2 = Order.builder()
                .member(testMember)
                .totalAmount(new BigDecimal("20000"))
                .build();

        orderRepository.save(order1);
        orderRepository.save(order2);

        // when & then
        mockMvc.perform(get("/api/orders/member/{memberId}", testMember.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].memberId", everyItem(equalTo(testMember.getId().intValue()))))
                .andExpect(jsonPath("$[*].totalAmount", containsInAnyOrder(10000, 20000)));
    }

    @Test
    @DisplayName("전체 주문 목록 조회 성공")
    void getAllOrders_Success() throws Exception {
        // given
        Order order1 = Order.builder()
                .member(testMember)
                .totalAmount(new BigDecimal("15000"))
                .build();

        Order order2 = Order.builder()
                .member(testMember)
                .totalAmount(new BigDecimal("25000"))
                .build();

        orderRepository.save(order1);
        orderRepository.save(order2);

        // when & then
        mockMvc.perform(get("/api/orders"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].status", everyItem(equalTo("CONFIRMED"))));
    }

    @Test
    @DisplayName("주문 상태 업데이트 성공")
    void updateOrderStatus_Success() throws Exception {
        // given
        Order order = Order.builder()
                .member(testMember)
                .totalAmount(new BigDecimal("30000"))
                .build();
        Order savedOrder = orderRepository.save(order);

        // when & then
        mockMvc.perform(patch("/api/orders/{id}/status", savedOrder.getId())
                        .param("status", "SHIPPED"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedOrder.getId()))
                .andExpect(jsonPath("$.status").value("SHIPPED"));
    }

    @Test
    @DisplayName("주문 취소 성공")
    void cancelOrder_Success() throws Exception {
        // given
        Order order = Order.builder()
                .member(testMember)
                .totalAmount(new BigDecimal("30000"))
                .build();
        Order savedOrder = orderRepository.save(order);

        // when & then
        mockMvc.perform(patch("/api/orders/{id}/cancel", savedOrder.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedOrder.getId()))
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    @DisplayName("주문 삭제 성공")
    void deleteOrder_Success() throws Exception {
        // given
        Order order = Order.builder()
                .member(testMember)
                .totalAmount(new BigDecimal("30000"))
                .build();
        Order savedOrder = orderRepository.save(order);

        // when & then
        mockMvc.perform(delete("/api/orders/{id}", savedOrder.getId()))
                .andDo(print())
                .andExpect(status().isNoContent());

        // verify deletion
        mockMvc.perform(get("/api/orders/{id}", savedOrder.getId()))
                .andExpect(status().isNotFound());
    }
}