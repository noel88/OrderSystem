package kr.minigate.ordersystem.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.minigate.ordersystem.api.request.MemberCreateRequest;
import kr.minigate.ordersystem.domain.Member;
import kr.minigate.ordersystem.domain.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
class MemberApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("회원 등록 성공")
    void createMember_Success() throws Exception {
        // given
        MemberCreateRequest request = new MemberCreateRequest("홍길동", "hong@example.com", "010-1234-5678", "서울시 강남구");

        // when & then
        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("홍길동"))
                .andExpect(jsonPath("$.email").value("hong@example.com"))
                .andExpect(jsonPath("$.phone").value("010-1234-5678"))
                .andExpect(jsonPath("$.address").value("서울시 강남구"))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @DisplayName("회원 등록 실패 - 이름 없음")
    void createMember_Fail_NoName() throws Exception {
        // given
        MemberCreateRequest request = new MemberCreateRequest(null, "test@example.com", "010-1234-5678", "서울시");

        // when & then
        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("회원 등록 실패 - 이메일 형식 오류")
    void createMember_Fail_InvalidEmail() throws Exception {
        // given
        MemberCreateRequest request = new MemberCreateRequest("테스트", "invalid-email", "010-1234-5678", "서울시");

        // when & then
        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("회원 등록 실패 - 중복 이메일")
    void createMember_Fail_DuplicateEmail() throws Exception {
        // given
        Member existingMember = Member.builder()
                .name("기존회원")
                .email("duplicate@example.com")
                .phone("010-0000-0000")
                .address("기존주소")
                .build();
        memberRepository.save(existingMember);

        MemberCreateRequest request = new MemberCreateRequest("신규회원", "duplicate@example.com", "010-1111-1111", "신규주소");

        // when & then
        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("회원 조회 성공")
    void getMember_Success() throws Exception {
        // given
        Member member = Member.builder()
                .name("조회테스트")
                .email("get@example.com")
                .phone("010-1234-5678")
                .address("서울시 서초구")
                .build();
        Member savedMember = memberRepository.save(member);

        // when & then
        mockMvc.perform(get("/api/members/{id}", savedMember.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedMember.getId()))
                .andExpect(jsonPath("$.name").value("조회테스트"))
                .andExpect(jsonPath("$.email").value("get@example.com"))
                .andExpect(jsonPath("$.phone").value("010-1234-5678"))
                .andExpect(jsonPath("$.address").value("서울시 서초구"));
    }

    @Test
    @DisplayName("회원 조회 실패 - 존재하지 않는 ID")
    void getMember_Fail_NotFound() throws Exception {
        // when & then
        mockMvc.perform(get("/api/members/{id}", 999999L))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("전체 회원 목록 조회 성공")
    void getAllMembers_Success() throws Exception {
        // given
        Member member1 = Member.builder()
                .name("회원1")
                .email("member1@example.com")
                .phone("010-1111-1111")
                .address("주소1")
                .build();

        Member member2 = Member.builder()
                .name("회원2")
                .email("member2@example.com")
                .phone("010-2222-2222")
                .address("주소2")
                .build();

        memberRepository.save(member1);
        memberRepository.save(member2);

        // when & then
        mockMvc.perform(get("/api/members"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("회원1", "회원2")))
                .andExpect(jsonPath("$[*].email", containsInAnyOrder("member1@example.com", "member2@example.com")));
    }

    @Test
    @DisplayName("회원 목록 조회 - 빈 목록")
    void getAllMembers_EmptyList() throws Exception {
        // when & then
        mockMvc.perform(get("/api/members"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("회원 정보 수정 성공")
    void updateMember_Success() throws Exception {
        // given
        Member member = Member.builder()
                .name("수정전")
                .email("update@example.com")
                .phone("010-1111-1111")
                .address("수정전 주소")
                .build();
        Member savedMember = memberRepository.save(member);

        MemberCreateRequest updateRequest = new MemberCreateRequest("수정후", "update@example.com", "010-2222-2222", "수정후 주소");

        // when & then
        mockMvc.perform(put("/api/members/{id}", savedMember.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("수정후"))
                .andExpect(jsonPath("$.phone").value("010-2222-2222"))
                .andExpect(jsonPath("$.address").value("수정후 주소"));
    }

    @Test
    @DisplayName("회원 삭제 성공")
    void deleteMember_Success() throws Exception {
        // given
        Member member = Member.builder()
                .name("삭제대상")
                .email("delete@example.com")
                .phone("010-1111-1111")
                .address("삭제 주소")
                .build();
        Member savedMember = memberRepository.save(member);

        // when & then
        mockMvc.perform(delete("/api/members/{id}", savedMember.getId()))
                .andDo(print())
                .andExpect(status().isNoContent());

        // verify deletion
        mockMvc.perform(get("/api/members/{id}", savedMember.getId()))
                .andExpect(status().isNotFound());
    }
}