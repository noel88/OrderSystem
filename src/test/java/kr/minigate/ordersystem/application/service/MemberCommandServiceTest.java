package kr.minigate.ordersystem.application.service;

import kr.minigate.ordersystem.application.dto.MemberCreateCommand;
import kr.minigate.ordersystem.application.dto.MemberQuery;
import kr.minigate.ordersystem.domain.Member;
import kr.minigate.ordersystem.domain.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class MemberCommandServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberCommandServiceImpl memberCommandService;

    @Test
    void 회원생성_성공() {
        // given
        MemberCreateCommand command = new MemberCreateCommand(
            "홍길동", "hong@test.com", "010-1234-5678", "서울시 강남구"
        );

        Member savedMember = Member.builder()
            .name(command.name())
            .email(command.email())
            .phone(command.phone())
            .address(command.address())
            .build();

        given(memberRepository.existsByEmail(command.email())).willReturn(false);
        given(memberRepository.save(any(Member.class))).willReturn(savedMember);

        // when
        MemberQuery result = memberCommandService.createMember(command);

        // then
        assertThat(result.name()).isEqualTo("홍길동");
        assertThat(result.email()).isEqualTo("hong@test.com");
        assertThat(result.phone()).isEqualTo("010-1234-5678");
        assertThat(result.address()).isEqualTo("서울시 강남구");

        then(memberRepository).should().existsByEmail(command.email());
        then(memberRepository).should().save(any(Member.class));
    }

    @Test
    void 회원생성_실패_중복_이메일() {
        // given
        MemberCreateCommand command = new MemberCreateCommand(
            "홍길동", "hong@test.com", "010-1234-5678", "서울시 강남구"
        );

        given(memberRepository.existsByEmail(command.email())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> memberCommandService.createMember(command))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("이미 존재하는 이메일입니다");

        then(memberRepository).should().existsByEmail(command.email());
        then(memberRepository).shouldHaveNoMoreInteractions();
    }
}