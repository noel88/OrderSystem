package kr.minigate.ordersystem.docs;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.minigate.ordersystem.api.controller.OrderController;
import kr.minigate.ordersystem.api.request.OrderCreateRequest;
import kr.minigate.ordersystem.application.dto.OrderCreateCommand;
import kr.minigate.ordersystem.application.dto.OrderQuery;
import kr.minigate.ordersystem.application.service.OrderCommandService;
import kr.minigate.ordersystem.application.service.OrderQueryService;
import kr.minigate.ordersystem.domain.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(OrderController.class)
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
class OrderControllerDocsTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @MockBean
    private OrderCommandService orderCommandService;

    @MockBean
    private OrderQueryService orderQueryService;

    @BeforeEach
    void setUp() {
        // 기본 Mock 설정
        OrderQuery mockOrderQuery = new OrderQuery(
            1L, 1L, "홍길동", new BigDecimal("1200000"), OrderStatus.CONFIRMED,
            List.of(), LocalDateTime.now()
        );

        when(orderCommandService.createOrder(any(OrderCreateCommand.class)))
            .thenReturn(mockOrderQuery);
        when(orderQueryService.getOrder(anyLong()))
            .thenReturn(mockOrderQuery);
        when(orderQueryService.getAllOrders())
            .thenReturn(Arrays.asList(mockOrderQuery));
        when(orderQueryService.getOrdersByMemberId(anyLong()))
            .thenReturn(Arrays.asList(mockOrderQuery));
    }

    @Test
    void createOrder() throws Exception {
        OrderCreateRequest.OrderItemRequest item1 = new OrderCreateRequest.OrderItemRequest(1L, 2);
        OrderCreateRequest.OrderItemRequest item2 = new OrderCreateRequest.OrderItemRequest(2L, 1);
        OrderCreateRequest request = new OrderCreateRequest(1L, Arrays.asList(item1, item2));

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andDo(document("order-create",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestFields(
                        fieldWithPath("memberId").type(JsonFieldType.NUMBER)
                            .description("주문자 회원 ID"),
                        fieldWithPath("orderItems").type(JsonFieldType.ARRAY)
                            .description("주문 상품 목록"),
                        fieldWithPath("orderItems[].productId").type(JsonFieldType.NUMBER)
                            .description("상품 ID"),
                        fieldWithPath("orderItems[].quantity").type(JsonFieldType.NUMBER)
                            .description("주문 수량")
                    ),
                    responseFields(
                        fieldWithPath("id").type(JsonFieldType.NUMBER)
                            .description("주문 ID"),
                        fieldWithPath("memberId").type(JsonFieldType.NUMBER)
                            .description("주문자 회원 ID"),
                        fieldWithPath("memberName").type(JsonFieldType.STRING)
                            .description("주문자 이름"),
                        fieldWithPath("totalAmount").type(JsonFieldType.NUMBER)
                            .description("총 주문 금액"),
                        fieldWithPath("status").type(JsonFieldType.STRING)
                            .description("주문 상태 (CONFIRMED, SHIPPED, DELIVERED, CANCELLED)"),
                        fieldWithPath("orderItems").type(JsonFieldType.ARRAY)
                            .description("주문 상품 목록"),
                        fieldWithPath("createdAt").type(JsonFieldType.STRING)
                            .description("주문 생성일시")
                    )
                ));
    }

    @Test
    void getOrder() throws Exception {
        mockMvc.perform(get("/api/orders/{id}", 1L))
                .andExpect(status().isOk())
                .andDo(document("order-get",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    pathParameters(
                        parameterWithName("id").description("조회할 주문 ID")
                    ),
                    responseFields(
                        fieldWithPath("id").type(JsonFieldType.NUMBER)
                            .description("주문 ID"),
                        fieldWithPath("memberId").type(JsonFieldType.NUMBER)
                            .description("주문자 회원 ID"),
                        fieldWithPath("memberName").type(JsonFieldType.STRING)
                            .description("주문자 이름"),
                        fieldWithPath("totalAmount").type(JsonFieldType.NUMBER)
                            .description("총 주문 금액"),
                        fieldWithPath("status").type(JsonFieldType.STRING)
                            .description("주문 상태"),
                        fieldWithPath("orderItems").type(JsonFieldType.ARRAY)
                            .description("주문 상품 목록"),
                        fieldWithPath("createdAt").type(JsonFieldType.STRING)
                            .description("주문 생성일시")
                    )
                ));
    }

    @Test
    void getAllOrders() throws Exception {
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andDo(document("order-list",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    responseFields(
                        fieldWithPath("[]").type(JsonFieldType.ARRAY)
                            .description("주문 목록"),
                        fieldWithPath("[].id").type(JsonFieldType.NUMBER)
                            .description("주문 ID"),
                        fieldWithPath("[].memberId").type(JsonFieldType.NUMBER)
                            .description("주문자 회원 ID"),
                        fieldWithPath("[].memberName").type(JsonFieldType.STRING)
                            .description("주문자 이름"),
                        fieldWithPath("[].totalAmount").type(JsonFieldType.NUMBER)
                            .description("총 주문 금액"),
                        fieldWithPath("[].status").type(JsonFieldType.STRING)
                            .description("주문 상태"),
                        fieldWithPath("[].orderItems").type(JsonFieldType.ARRAY)
                            .description("주문 상품 목록"),
                        fieldWithPath("[].createdAt").type(JsonFieldType.STRING)
                            .description("주문 생성일시")
                    )
                ));
    }

    @Test
    void getOrdersByMember() throws Exception {
        mockMvc.perform(get("/api/orders")
                .param("memberId", "1"))
                .andExpect(status().isOk())
                .andDo(document("order-list-by-member",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    queryParameters(
                        parameterWithName("memberId").description("조회할 회원 ID").optional()
                    ),
                    responseFields(
                        fieldWithPath("[]").type(JsonFieldType.ARRAY)
                            .description("해당 회원의 주문 목록"),
                        fieldWithPath("[].id").type(JsonFieldType.NUMBER)
                            .description("주문 ID"),
                        fieldWithPath("[].memberId").type(JsonFieldType.NUMBER)
                            .description("주문자 회원 ID"),
                        fieldWithPath("[].memberName").type(JsonFieldType.STRING)
                            .description("주문자 이름"),
                        fieldWithPath("[].totalAmount").type(JsonFieldType.NUMBER)
                            .description("총 주문 금액"),
                        fieldWithPath("[].status").type(JsonFieldType.STRING)
                            .description("주문 상태"),
                        fieldWithPath("[].orderItems").type(JsonFieldType.ARRAY)
                            .description("주문 상품 목록"),
                        fieldWithPath("[].createdAt").type(JsonFieldType.STRING)
                            .description("주문 생성일시")
                    )
                ));
    }
}