package kr.minigate.ordersystem.api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@WebMvcTest(MemberController.class)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void 회원가입_성공() throws Exception {
        // given
        String memberJson = """
            {
                "name": "홍길동",
                "email": "hong@test.com",
                "phone": "010-1234-5678",
                "address": "서울시 강남구"
            }
            """;

        // when & then
        mockMvc.perform(post("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(memberJson))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("홍길동"))
                .andExpect(jsonPath("$.email").value("hong@test.com"))
                .andExpect(jsonPath("$.phone").value("010-1234-5678"))
                .andExpect(jsonPath("$.address").value("서울시 강남구"));
    }

    @Test
    void 회원가입_실패_이름_누락() throws Exception {
        // given
        String memberJson = """
            {
                "email": "hong@test.com",
                "phone": "010-1234-5678",
                "address": "서울시 강남구"
            }
            """;

        // when & then
        mockMvc.perform(post("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(memberJson))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void 회원가입_실패_이메일_형식_오류() throws Exception {
        // given
        String memberJson = """
            {
                "name": "홍길동",
                "email": "invalid-email",
                "phone": "010-1234-5678",
                "address": "서울시 강남구"
            }
            """;

        // when & then
        mockMvc.perform(post("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(memberJson))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void 회원조회_성공() throws Exception {
        // when & then
        mockMvc.perform(get("/api/members/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("홍길동"))
                .andExpect(jsonPath("$.email").value("hong@test.com"));
    }

    @Test
    void 회원조회_실패_존재하지_않는_회원() throws Exception {
        // when & then
        mockMvc.perform(get("/api/members/999"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void 회원조회_실패_잘못된_ID_형식() throws Exception {
        // when & then
        mockMvc.perform(get("/api/members/invalid"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void 회원가입_실패_서버_오류() throws Exception {
        // given - 서버 오류를 유발하는 이메일
        String memberJson = """
            {
                "name": "서버오류테스트",
                "email": "server-error@test.com",
                "phone": "010-9999-9999",
                "address": "서버 오류 테스트"
            }
            """;

        // when & then
        mockMvc.perform(post("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(memberJson))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }

    @Test
    void 회원조회_실패_서버_오류() throws Exception {
        // when & then - ID 500은 서버 오류를 발생시킴
        mockMvc.perform(get("/api/members/500"))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }
}