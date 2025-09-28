package kr.minigate.ordersystem.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.minigate.ordersystem.api.request.PaymentCreateRequest;
import kr.minigate.ordersystem.domain.*;
import kr.minigate.ordersystem.domain.repository.MemberRepository;
import kr.minigate.ordersystem.domain.repository.OrderRepository;
import kr.minigate.ordersystem.domain.repository.PaymentRepository;
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
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(locations = "classpath:application-test.yml")
class PaymentApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Order testOrder;
    private Member testMember;

    @BeforeEach
    void setUp() {
        paymentRepository.deleteAll();
        orderRepository.deleteAll();
        memberRepository.deleteAll();

        // 테스트 회원 생성
        testMember = Member.builder()
                .name("테스트회원")
                .email("test@example.com")
                .phone("010-1234-5678")
                .address("서울시 강남구")
                .build();
        testMember = memberRepository.save(testMember);

        // 테스트 주문 생성
        testOrder = Order.builder()
                .member(testMember)
                .totalAmount(new BigDecimal("50000"))
                .build();
        testOrder = orderRepository.save(testOrder);
    }

    @Test
    @DisplayName("결제 생성 성공")
    void createPayment_Success() throws Exception {
        // given
        PaymentCreateRequest request = new PaymentCreateRequest(testOrder.getId(), PaymentMethod.CARD);

        // when & then
        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").value(testOrder.getId()))
                .andExpect(jsonPath("$.amount").value(50000))
                .andExpect(jsonPath("$.paymentMethod").value("CARD"))
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.transactionId").exists())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @DisplayName("결제 생성 실패 - 주문 없음")
    void createPayment_Fail_OrderNotFound() throws Exception {
        // given
        PaymentCreateRequest request = new PaymentCreateRequest(999999L, PaymentMethod.CARD);

        // when & then
        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("결제 생성 실패 - 중복 결제")
    void createPayment_Fail_DuplicatePayment() throws Exception {
        // given
        Payment existingPayment = Payment.builder()
                .order(testOrder)
                .amount(testOrder.getTotalAmount())
                .paymentMethod(PaymentMethod.CARD)
                .transactionId(UUID.randomUUID().toString())
                .build();
        paymentRepository.save(existingPayment);

        PaymentCreateRequest request = new PaymentCreateRequest(testOrder.getId(), PaymentMethod.BANK_TRANSFER);

        // when & then
        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("결제 생성 성공 - 계좌이체")
    void createPayment_Success_BankTransfer() throws Exception {
        // given
        PaymentCreateRequest request = new PaymentCreateRequest(testOrder.getId(), PaymentMethod.BANK_TRANSFER);

        // when & then
        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").value(testOrder.getId()))
                .andExpect(jsonPath("$.amount").value(50000))
                .andExpect(jsonPath("$.paymentMethod").value("BANK_TRANSFER"))
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    @DisplayName("결제 조회 성공")
    void getPayment_Success() throws Exception {
        // given
        Payment payment = Payment.builder()
                .order(testOrder)
                .amount(testOrder.getTotalAmount())
                .paymentMethod(PaymentMethod.CARD)
                .transactionId(UUID.randomUUID().toString())
                .build();
        Payment savedPayment = paymentRepository.save(payment);

        // when & then
        mockMvc.perform(get("/api/payments/{id}", savedPayment.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedPayment.getId()))
                .andExpect(jsonPath("$.orderId").value(testOrder.getId()))
                .andExpect(jsonPath("$.amount").value(50000))
                .andExpect(jsonPath("$.paymentMethod").value("CARD"))
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.transactionId").value(savedPayment.getTransactionId()));
    }

    @Test
    @DisplayName("결제 조회 실패 - 존재하지 않는 ID")
    void getPayment_Fail_NotFound() throws Exception {
        // when & then
        mockMvc.perform(get("/api/payments/{id}", 999999L))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("주문별 결제 조회 성공")
    void getPaymentByOrder_Success() throws Exception {
        // given
        Payment payment = Payment.builder()
                .order(testOrder)
                .amount(testOrder.getTotalAmount())
                .paymentMethod(PaymentMethod.MOBILE_PAY)
                .transactionId(UUID.randomUUID().toString())
                .build();
        paymentRepository.save(payment);

        // when & then
        mockMvc.perform(get("/api/payments/order/{orderId}", testOrder.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(testOrder.getId()))
                .andExpect(jsonPath("$.paymentMethod").value("MOBILE_PAY"))
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    @DisplayName("주문별 결제 조회 실패 - 결제 없음")
    void getPaymentByOrder_Fail_NotFound() throws Exception {
        // when & then
        mockMvc.perform(get("/api/payments/order/{orderId}", testOrder.getId()))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("전체 결제 목록 조회 성공")
    void getAllPayments_Success() throws Exception {
        // given
        Payment payment1 = Payment.builder()
                .order(testOrder)
                .amount(testOrder.getTotalAmount())
                .paymentMethod(PaymentMethod.CARD)
                .transactionId(UUID.randomUUID().toString())
                .build();

        Order order2 = Order.builder()
                .member(testMember)
                .totalAmount(new BigDecimal("30000"))
                .build();
        order2 = orderRepository.save(order2);

        Payment payment2 = Payment.builder()
                .order(order2)
                .amount(order2.getTotalAmount())
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .transactionId(UUID.randomUUID().toString())
                .build();

        paymentRepository.save(payment1);
        paymentRepository.save(payment2);

        // when & then
        mockMvc.perform(get("/api/payments"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].status", everyItem(equalTo("COMPLETED"))))
                .andExpect(jsonPath("$[*].amount", containsInAnyOrder(50000, 30000)));
    }

    @Test
    @DisplayName("결제 취소 성공")
    void cancelPayment_Success() throws Exception {
        // given
        Payment payment = Payment.builder()
                .order(testOrder)
                .amount(testOrder.getTotalAmount())
                .paymentMethod(PaymentMethod.CARD)
                .transactionId(UUID.randomUUID().toString())
                .build();
        Payment savedPayment = paymentRepository.save(payment);

        // when & then
        mockMvc.perform(patch("/api/payments/{id}/cancel", savedPayment.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedPayment.getId()))
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    @DisplayName("결제 환불 성공")
    void refundPayment_Success() throws Exception {
        // given
        Payment payment = Payment.builder()
                .order(testOrder)
                .amount(testOrder.getTotalAmount())
                .paymentMethod(PaymentMethod.CARD)
                .transactionId(UUID.randomUUID().toString())
                .build();
        Payment savedPayment = paymentRepository.save(payment);

        // when & then
        mockMvc.perform(patch("/api/payments/{id}/refund", savedPayment.getId())
                        .param("refundAmount", "25000"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedPayment.getId()))
                .andExpect(jsonPath("$.status").value("REFUNDED"));
    }

    @Test
    @DisplayName("결제 삭제 성공")
    void deletePayment_Success() throws Exception {
        // given
        Payment payment = Payment.builder()
                .order(testOrder)
                .amount(testOrder.getTotalAmount())
                .paymentMethod(PaymentMethod.CARD)
                .transactionId(UUID.randomUUID().toString())
                .build();
        Payment savedPayment = paymentRepository.save(payment);

        // when & then
        mockMvc.perform(delete("/api/payments/{id}", savedPayment.getId()))
                .andDo(print())
                .andExpect(status().isNoContent());

        // verify deletion
        mockMvc.perform(get("/api/payments/{id}", savedPayment.getId()))
                .andExpect(status().isNotFound());
    }
}