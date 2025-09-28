package kr.minigate.ordersystem.api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void 상품등록_성공() throws Exception {
        // given
        String productJson = """
            {
                "name": "아이폰 15",
                "description": "애플 스마트폰",
                "price": 1200000,
                "stock": 10
            }
            """;

        // when & then
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("아이폰 15"))
                .andExpect(jsonPath("$.description").value("애플 스마트폰"))
                .andExpect(jsonPath("$.price").value(1200000))
                .andExpect(jsonPath("$.stock").value(10));
    }



    @Test
    void 상품등록_실패_이름_누락() throws Exception {
        // given
        String productJson = """
            {
                "description": "애플 스마트폰",
                "price": 1200000,
                "stock": 10
            }
            """;

        // when & then
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void 상품등록_실패_가격_음수() throws Exception {
        // given
        String productJson = """
            {
                "name": "아이폰 15",
                "description": "애플 스마트폰",
                "price": -1000,
                "stock": 10
            }
            """;

        // when & then
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void 상품목록조회_성공() throws Exception {
        // when & then
        mockMvc.perform(get("/api/products"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("아이폰 15"))
                .andExpect(jsonPath("$[1].name").value("갤럭시 S24"));
    }

    @Test
    void 상품조회_성공() throws Exception {
        // when & then
        mockMvc.perform(get("/api/products/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("아이폰 15"));
    }

    @Test
    void 상품등록_실패_서버_오류() throws Exception {
        // given
        String productJson = """
            {
                "name": "서버오류상품",
                "description": "서버 오류 테스트",
                "price": 1000000,
                "stock": 10
            }
            """;

        // when & then
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }

    @Test
    void 상품조회_실패_존재하지_않는_상품() throws Exception {
        // when & then
        mockMvc.perform(get("/api/products/999"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void 상품조회_실패_서버_오류() throws Exception {
        // when & then
        mockMvc.perform(get("/api/products/500"))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }
}