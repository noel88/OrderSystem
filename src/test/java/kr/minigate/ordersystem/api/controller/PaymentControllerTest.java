package kr.minigate.ordersystem.api.controller;

import kr.minigate.ordersystem.application.dto.PaymentCreateCommand;
import kr.minigate.ordersystem.application.dto.PaymentQuery;
import kr.minigate.ordersystem.application.service.PaymentCommandService;
import kr.minigate.ordersystem.application.service.PaymentQueryService;
import kr.minigate.ordersystem.domain.PaymentMethod;
import kr.minigate.ordersystem.domain.PaymentStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentCommandService paymentCommandService;

    @MockBean
    private PaymentQueryService paymentQueryService;

    @Test
    void 결제처리_성공_카드결제() throws Exception {
        // given
        String paymentJson = """
            {
                "orderId": 1,
                "paymentMethod": "CARD"
            }
            """;

        PaymentQuery mockResponse = new PaymentQuery(
            1L, 1L, new BigDecimal("1200000"), PaymentMethod.CARD,
            PaymentStatus.COMPLETED, "TXN_12345678",
            LocalDateTime.now()
        );

        when(paymentCommandService.processPayment(any(PaymentCreateCommand.class)))
            .thenReturn(mockResponse);

        // when & then
        mockMvc.perform(post("/api/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(paymentJson))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.amount").value(1200000))
                .andExpect(jsonPath("$.paymentMethod").value("CARD"))
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.transactionId").exists());
    }

    @Test
    void 결제처리_성공_계좌이체() throws Exception {
        // given
        String paymentJson = """
            {
                "orderId": 1,
                "paymentMethod": "BANK_TRANSFER"
            }
            """;

        PaymentQuery mockResponse = new PaymentQuery(
            1L, 1L, new BigDecimal("1200000"), PaymentMethod.BANK_TRANSFER,
            PaymentStatus.COMPLETED, "TXN_87654321",
            LocalDateTime.now()
        );

        when(paymentCommandService.processPayment(any(PaymentCreateCommand.class)))
            .thenReturn(mockResponse);

        // when & then
        mockMvc.perform(post("/api/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(paymentJson))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.amount").value(1200000))
                .andExpect(jsonPath("$.paymentMethod").value("BANK_TRANSFER"))
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.transactionId").exists());
    }

    @Test
    void 결제처리_실패_주문ID_누락() throws Exception {
        // given
        String paymentJson = """
            {
                "paymentMethod": "CARD"
            }
            """;

        // when & then
        mockMvc.perform(post("/api/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(paymentJson))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void 결제처리_실패_결제방법_누락() throws Exception {
        // given
        String paymentJson = """
            {
                "orderId": 1
            }
            """;

        // when & then
        mockMvc.perform(post("/api/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(paymentJson))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void 결제처리_실패_존재하지_않는_주문() throws Exception {
        // given
        String paymentJson = """
            {
                "orderId": 999,
                "paymentMethod": "CARD"
            }
            """;

        when(paymentCommandService.processPayment(any(PaymentCreateCommand.class)))
            .thenThrow(new IllegalArgumentException("존재하지 않는 주문입니다"));

        // when & then
        mockMvc.perform(post("/api/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(paymentJson))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void 결제조회_성공() throws Exception {
        // given
        PaymentQuery mockResponse = new PaymentQuery(
            1L, 1L, new BigDecimal("1200000"), PaymentMethod.CARD,
            PaymentStatus.COMPLETED, "TXN_12345678",
            LocalDateTime.now()
        );

        when(paymentQueryService.getPayment(1L))
            .thenReturn(mockResponse);

        // when & then
        mockMvc.perform(get("/api/payments/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.amount").value(1200000))
                .andExpect(jsonPath("$.paymentMethod").value("CARD"))
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void 결제조회_실패_존재하지_않는_결제() throws Exception {
        // given
        when(paymentQueryService.getPayment(999L))
            .thenThrow(new IllegalArgumentException("존재하지 않는 결제입니다"));

        // when & then
        mockMvc.perform(get("/api/payments/999"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void 주문별_결제조회_성공() throws Exception {
        // given
        PaymentQuery mockResponse = new PaymentQuery(
            1L, 1L, new BigDecimal("1200000"), PaymentMethod.CARD,
            PaymentStatus.COMPLETED, "TXN_12345678",
            LocalDateTime.now()
        );

        when(paymentQueryService.getPaymentByOrderId(1L))
            .thenReturn(mockResponse);

        // when & then
        mockMvc.perform(get("/api/payments/order/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.amount").value(1200000));
    }

    @Test
    void 주문별_결제조회_실패_존재하지_않는_주문() throws Exception {
        // given
        when(paymentQueryService.getPaymentByOrderId(999L))
            .thenThrow(new IllegalArgumentException("존재하지 않는 주문입니다"));

        // when & then
        mockMvc.perform(get("/api/payments/order/999"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void 결제처리_실패_서버_오류() throws Exception {
        // given
        String paymentJson = """
            {
                "orderId": 500,
                "paymentMethod": "CARD"
            }
            """;

        when(paymentCommandService.processPayment(any(PaymentCreateCommand.class)))
            .thenThrow(new RuntimeException("서버 내부 오류"));

        // when & then
        mockMvc.perform(post("/api/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(paymentJson))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }

    @Test
    void 결제조회_실패_서버_오류() throws Exception {
        // given
        when(paymentQueryService.getPayment(500L))
            .thenThrow(new RuntimeException("서버 내부 오류"));

        // when & then
        mockMvc.perform(get("/api/payments/500"))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }

    @Test
    void 주문별_결제조회_실패_서버_오류() throws Exception {
        // given
        when(paymentQueryService.getPaymentByOrderId(500L))
            .thenThrow(new RuntimeException("서버 내부 오류"));

        // when & then
        mockMvc.perform(get("/api/payments/order/500"))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }
}