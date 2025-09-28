package kr.minigate.ordersystem.api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void 결제처리_성공_카드결제() throws Exception {
        // given
        String paymentJson = """
            {
                "orderId": 1,
                "paymentMethod": "CARD"
            }
            """;

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

        // when & then
        mockMvc.perform(post("/api/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(paymentJson))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.paymentMethod").value("BANK_TRANSFER"))
                .andExpect(jsonPath("$.status").value("COMPLETED"));
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
    void 주문별_결제조회_성공() throws Exception {
        // when & then
        mockMvc.perform(get("/api/payments/order/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.amount").value(1200000))
                .andExpect(jsonPath("$.paymentMethod").value("CARD"))
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.transactionId").value("TXN_12345678"));
    }

    @Test
    void 결제조회_성공() throws Exception {
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
    void 결제처리_실패_존재하지_않는_주문() throws Exception {
        // given
        String paymentJson = """
            {
                "orderId": 999,
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
    void 결제처리_실패_서버_오류() throws Exception {
        // given
        String paymentJson = """
            {
                "orderId": 500,
                "paymentMethod": "CARD"
            }
            """;

        // when & then
        mockMvc.perform(post("/api/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(paymentJson))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }

    @Test
    void 주문별_결제조회_실패_존재하지_않는_주문() throws Exception {
        // when & then
        mockMvc.perform(get("/api/payments/order/999"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void 주문별_결제조회_실패_서버_오류() throws Exception {
        // when & then
        mockMvc.perform(get("/api/payments/order/500"))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }

    @Test
    void 결제조회_실패_존재하지_않는_결제() throws Exception {
        // when & then
        mockMvc.perform(get("/api/payments/999"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void 결제조회_실패_서버_오류() throws Exception {
        // when & then
        mockMvc.perform(get("/api/payments/500"))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }
}