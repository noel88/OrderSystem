package kr.minigate.ordersystem.application.service;

import kr.minigate.ordersystem.application.dto.MemberQuery;
import kr.minigate.ordersystem.domain.Member;
import kr.minigate.ordersystem.domain.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class MemberQueryServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberQueryServiceImpl memberQueryService;

    @Test
    void 모든_회원_조회_성공() {
        // given
        List<Member> members = List.of(
            Member.builder().name("홍길동").email("hong@test.com").phone("010-1234-5678").address("서울시 강남구").build(),
            Member.builder().name("김영희").email("kim@test.com").phone("010-9876-5432").address("부산시 해운대구").build()
        );

        given(memberRepository.findAll()).willReturn(members);

        // when
        List<MemberQuery> result = memberQueryService.getAllMembers();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).name()).isEqualTo("홍길동");
        assertThat(result.get(1).name()).isEqualTo("김영희");

        then(memberRepository).should().findAll();
    }

    @Test
    void 회원_조회_성공() {
        // given
        Long memberId = 1L;
        Member member = Member.builder()
            .name("홍길동")
            .email("hong@test.com")
            .phone("010-1234-5678")
            .address("서울시 강남구")
            .build();

        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));

        // when
        MemberQuery result = memberQueryService.getMember(memberId);

        // then
        assertThat(result.name()).isEqualTo("홍길동");
        assertThat(result.email()).isEqualTo("hong@test.com");
        assertThat(result.phone()).isEqualTo("010-1234-5678");
        assertThat(result.address()).isEqualTo("서울시 강남구");

        then(memberRepository).should().findById(memberId);
    }

    @Test
    void 회원_조회_실패_존재하지_않는_회원() {
        // given
        Long memberId = 999L;

        given(memberRepository.findById(memberId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> memberQueryService.getMember(memberId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("존재하지 않는 회원입니다");

        then(memberRepository).should().findById(memberId);
    }
}