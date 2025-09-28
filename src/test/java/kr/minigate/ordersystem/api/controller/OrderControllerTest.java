package kr.minigate.ordersystem.api.controller;

import kr.minigate.ordersystem.application.dto.OrderCreateCommand;
import kr.minigate.ordersystem.application.dto.OrderQuery;
import kr.minigate.ordersystem.application.service.OrderCommandService;
import kr.minigate.ordersystem.application.service.OrderQueryService;
import kr.minigate.ordersystem.domain.OrderStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderCommandService orderCommandService;

    @MockBean
    private OrderQueryService orderQueryService;

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

        OrderQuery mockResponse = new OrderQuery(
            1L, 1L, "홍길동", new BigDecimal("1200000"), OrderStatus.CONFIRMED,
            List.of(), LocalDateTime.now()
        );

        when(orderCommandService.createOrder(any(OrderCreateCommand.class)))
            .thenReturn(mockResponse);

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
    void 주문생성_실패_존재하지_않는_회원() throws Exception {
        // given
        String orderJson = """
            {
                "memberId": 999,
                "orderItems": [
                    {
                        "productId": 1,
                        "quantity": 2
                    }
                ]
            }
            """;

        when(orderCommandService.createOrder(any(OrderCreateCommand.class)))
            .thenThrow(new IllegalArgumentException("존재하지 않는 회원입니다"));

        // when & then
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(orderJson))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void 주문조회_성공() throws Exception {
        // given
        OrderQuery mockResponse = new OrderQuery(
            1L, 1L, "홍길동", new BigDecimal("1200000"), OrderStatus.CONFIRMED,
            List.of(), LocalDateTime.now()
        );

        when(orderQueryService.getOrder(1L))
            .thenReturn(mockResponse);

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
    void 주문조회_실패_존재하지_않는_주문() throws Exception {
        // given
        when(orderQueryService.getOrder(999L))
            .thenThrow(new IllegalArgumentException("존재하지 않는 주문입니다"));

        // when & then
        mockMvc.perform(get("/api/orders/999"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void 내_주문목록조회_성공() throws Exception {
        // given
        List<OrderQuery> mockResponse = List.of(
            new OrderQuery(1L, 1L, "홍길동", new BigDecimal("1200000"), OrderStatus.CONFIRMED,
                List.of(), LocalDateTime.now())
        );

        when(orderQueryService.getOrdersByMemberId(1L))
            .thenReturn(mockResponse);

        // when & then
        mockMvc.perform(get("/api/orders").param("memberId", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].memberId").value(1))
                .andExpect(jsonPath("$[0].memberName").value("홍길동"));
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
                        "quantity": 2
                    }
                ]
            }
            """;

        when(orderCommandService.createOrder(any(OrderCreateCommand.class)))
            .thenThrow(new RuntimeException("서버 내부 오류"));

        // when & then
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(orderJson))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }

    @Test
    void 주문조회_실패_서버_오류() throws Exception {
        // given
        when(orderQueryService.getOrder(500L))
            .thenThrow(new RuntimeException("서버 내부 오류"));

        // when & then
        mockMvc.perform(get("/api/orders/500"))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }

    @Test
    void 주문목록조회_실패_서버_오류() throws Exception {
        // given
        when(orderQueryService.getOrdersByMemberId(500L))
            .thenThrow(new RuntimeException("서버 내부 오류"));

        // when & then
        mockMvc.perform(get("/api/orders").param("memberId", "500"))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }
}