package kr.minigate.ordersystem.api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void 주문생성_성공() throws Exception {
        // given
        String orderJson = """
            {
                "memberId": 1,
                "orderItems": [
                    {
                        "productId": 1,
                        "quantity": 2
                    },
                    {
                        "productId": 2,
                        "quantity": 1
                    }
                ]
            }
            """;

        // when & then
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(orderJson))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.memberId").value(1))
                .andExpect(jsonPath("$.memberName").value("홍길동"))
                .andExpect(jsonPath("$.totalAmount").value(1200000))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    void 주문생성_실패_회원ID_누락() throws Exception {
        // given
        String orderJson = """
            {
                "orderItems": [
                    {
                        "productId": 1,
                        "quantity": 2
                    }
                ]
            }
            """;

        // when & then
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(orderJson))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void 주문생성_실패_주문상품_없음() throws Exception {
        // given
        String orderJson = """
            {
                "memberId": 1,
                "orderItems": []
            }
            """;

        // when & then
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(orderJson))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void 주문생성_실패_상품ID_누락() throws Exception {
        // given
        String orderJson = """
            {
                "memberId": 1,
                "orderItems": [
                    {
                        "quantity": 2
                    }
                ]
            }
            """;

        // when & then
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(orderJson))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void 주문생성_실패_수량_음수() throws Exception {
        // given
        String orderJson = """
            {
                "memberId": 1,
                "orderItems": [
                    {
                        "productId": 1,
                        "quantity": -1
                    }
                ]
            }
            """;

        // when & then
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(orderJson))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void 내_주문목록조회_성공() throws Exception {
        // when & then
        mockMvc.perform(get("/api/orders")
                .param("memberId", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].memberId").value(1))
                .andExpect(jsonPath("$[0].status").value("CONFIRMED"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].status").value("SHIPPED"));
    }

    @Test
    void 주문조회_성공() throws Exception {
        // when & then
        mockMvc.perform(get("/api/orders/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.memberId").value(1))
                .andExpect(jsonPath("$.memberName").value("홍길동"))
                .andExpect(jsonPath("$.totalAmount").value(1200000))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    void 주문생성_실패_존재하지_않는_회원() throws Exception {
        // given
        String orderJson = """
            {
                "memberId": 999,
                "orderItems": [
                    {
                        "productId": 1,
                        "quantity": 1
                    }
                ]
            }
            """;

        // when & then
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(orderJson))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void 주문생성_실패_서버_오류() throws Exception {
        // given
        String orderJson = """
            {
                "memberId": 500,
                "orderItems": [
                    {
                        "productId": 1,
                        "quantity": 1
                    }
                ]
            }
            """;

        // when & then
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(orderJson))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }

    @Test
    void 주문목록조회_실패_서버_오류() throws Exception {
        // when & then
        mockMvc.perform(get("/api/orders")
                .param("memberId", "500"))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }

    @Test
    void 주문조회_실패_존재하지_않는_주문() throws Exception {
        // when & then
        mockMvc.perform(get("/api/orders/999"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void 주문조회_실패_서버_오류() throws Exception {
        // when & then
        mockMvc.perform(get("/api/orders/500"))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }
}