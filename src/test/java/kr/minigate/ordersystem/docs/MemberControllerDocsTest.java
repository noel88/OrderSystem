package kr.minigate.ordersystem.docs;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.minigate.ordersystem.api.controller.MemberController;
import kr.minigate.ordersystem.api.request.MemberCreateRequest;
import kr.minigate.ordersystem.application.dto.MemberCreateCommand;
import kr.minigate.ordersystem.application.dto.MemberQuery;
import kr.minigate.ordersystem.application.service.MemberCommandService;
import kr.minigate.ordersystem.application.service.MemberQueryService;
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
@WebMvcTest(MemberController.class)
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
class MemberControllerDocsTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @MockBean
    private MemberCommandService memberCommandService;

    @MockBean
    private MemberQueryService memberQueryService;

    @BeforeEach
    void setUp() {
        // 기본 Mock 설정
        MemberQuery mockMemberQuery = new MemberQuery(
            1L, "홍길동", "hong@example.com", "010-1234-5678", "서울시 강남구", LocalDateTime.now()
        );

        when(memberCommandService.createMember(any()))
            .thenReturn(mockMemberQuery);
        when(memberQueryService.getMember(any()))
            .thenReturn(mockMemberQuery);
        when(memberQueryService.getAllMembers())
            .thenReturn(Arrays.asList(mockMemberQuery));
    }

    @Test
    void createMember() throws Exception {
        MemberCreateRequest request = new MemberCreateRequest(
            "홍길동", "hong@example.com", "010-1234-5678", "서울시 강남구"
        );

        mockMvc.perform(post("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andDo(document("member-create",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestFields(
                        fieldWithPath("name").type(JsonFieldType.STRING)
                            .description("회원 이름"),
                        fieldWithPath("email").type(JsonFieldType.STRING)
                            .description("회원 이메일 (고유값)"),
                        fieldWithPath("phone").type(JsonFieldType.STRING)
                            .description("회원 전화번호"),
                        fieldWithPath("address").type(JsonFieldType.STRING)
                            .description("회원 주소")
                    ),
                    responseFields(
                        fieldWithPath("id").type(JsonFieldType.NUMBER)
                            .description("회원 ID"),
                        fieldWithPath("name").type(JsonFieldType.STRING)
                            .description("회원 이름"),
                        fieldWithPath("email").type(JsonFieldType.STRING)
                            .description("회원 이메일"),
                        fieldWithPath("phone").type(JsonFieldType.STRING)
                            .description("회원 전화번호"),
                        fieldWithPath("address").type(JsonFieldType.STRING)
                            .description("회원 주소"),
                        fieldWithPath("createdAt").type(JsonFieldType.STRING)
                            .description("회원 생성일시")
                    )
                ));
    }

    @Test
    void getMember() throws Exception {
        mockMvc.perform(get("/api/members/{id}", 1L))
                .andExpect(status().isOk())
                .andDo(document("member-get",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    pathParameters(
                        parameterWithName("id").description("조회할 회원 ID")
                    ),
                    responseFields(
                        fieldWithPath("id").type(JsonFieldType.NUMBER)
                            .description("회원 ID"),
                        fieldWithPath("name").type(JsonFieldType.STRING)
                            .description("회원 이름"),
                        fieldWithPath("email").type(JsonFieldType.STRING)
                            .description("회원 이메일"),
                        fieldWithPath("phone").type(JsonFieldType.STRING)
                            .description("회원 전화번호"),
                        fieldWithPath("address").type(JsonFieldType.STRING)
                            .description("회원 주소"),
                        fieldWithPath("createdAt").type(JsonFieldType.STRING)
                            .description("회원 생성일시")
                    )
                ));
    }

    @Test
    void getAllMembers() throws Exception {
        mockMvc.perform(get("/api/members"))
                .andExpect(status().isOk())
                .andDo(document("member-list",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    responseFields(
                        fieldWithPath("[]").type(JsonFieldType.ARRAY)
                            .description("회원 목록"),
                        fieldWithPath("[].id").type(JsonFieldType.NUMBER)
                            .description("회원 ID"),
                        fieldWithPath("[].name").type(JsonFieldType.STRING)
                            .description("회원 이름"),
                        fieldWithPath("[].email").type(JsonFieldType.STRING)
                            .description("회원 이메일"),
                        fieldWithPath("[].phone").type(JsonFieldType.STRING)
                            .description("회원 전화번호"),
                        fieldWithPath("[].address").type(JsonFieldType.STRING)
                            .description("회원 주소"),
                        fieldWithPath("[].createdAt").type(JsonFieldType.STRING)
                            .description("회원 생성일시")
                    )
                ));
    }
}