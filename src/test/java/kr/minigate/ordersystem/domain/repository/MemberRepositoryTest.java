package kr.minigate.ordersystem.domain.repository;

import kr.minigate.ordersystem.domain.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.yml")
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("회원 저장 성공")
    void save_Member_Success() {
        // given
        Member member = createMember("홍길동", "hong@example.com", "010-1111-2222", "서울시 강남구");

        // when
        Member savedMember = memberRepository.save(member);

        // then
        assertThat(savedMember).isNotNull();
        assertThat(savedMember.getId()).isNotNull();
        assertThat(savedMember.getName()).isEqualTo("홍길동");
        assertThat(savedMember.getEmail()).isEqualTo("hong@example.com");
        assertThat(savedMember.getPhone()).isEqualTo("010-1111-2222");
        assertThat(savedMember.getAddress()).isEqualTo("서울시 강남구");
    }

    @Test
    @DisplayName("ID로 회원 조회")
    void findById_ExistingMember_ReturnsMember() {
        // given
        Member member = createMember("김철수", "kim@example.com", "010-3333-4444", "서울시 서초구");
        Member savedMember = memberRepository.save(member);

        // when
        Optional<Member> foundMember = memberRepository.findById(savedMember.getId());

        // then
        assertThat(foundMember).isPresent();
        assertThat(foundMember.get().getName()).isEqualTo("김철수");
        assertThat(foundMember.get().getEmail()).isEqualTo("kim@example.com");
    }

    @Test
    @DisplayName("이메일로 회원 조회")
    void findByEmail_ExistingEmail_ReturnsMember() {
        // given
        Member member = createMember("이영희", "lee@example.com", "010-5555-6666", "서울시 강동구");
        memberRepository.save(member);

        // when
        Optional<Member> foundMember = memberRepository.findByEmail("lee@example.com");

        // then
        assertThat(foundMember).isPresent();
        assertThat(foundMember.get().getName()).isEqualTo("이영희");
        assertThat(foundMember.get().getPhone()).isEqualTo("010-5555-6666");
    }

    @Test
    @DisplayName("존재하지 않는 이메일로 조회 시 빈 값 반환")
    void findByEmail_NonExistingEmail_ReturnsEmpty() {
        // given
        Member member = createMember("박민수", "park@example.com", "010-7777-8888", "서울시 송파구");
        memberRepository.save(member);

        // when
        Optional<Member> foundMember = memberRepository.findByEmail("nonexist@example.com");

        // then
        assertThat(foundMember).isEmpty();
    }

    @Test
    @DisplayName("전체 회원 조회")
    void findAll_ReturnsAllMembers() {
        // given
        Member member1 = createMember("회원1", "member1@example.com", "010-1111-1111", "주소1");
        Member member2 = createMember("회원2", "member2@example.com", "010-2222-2222", "주소2");
        Member member3 = createMember("회원3", "member3@example.com", "010-3333-3333", "주소3");

        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);

        // when
        List<Member> members = memberRepository.findAll();

        // then
        assertThat(members).hasSize(3);
    }

    @Test
    @DisplayName("회원 정보 조회 및 검증")
    void findAndVerify_Member_Success() {
        // given
        Member member = createMember("테스트회원", "test@example.com", "010-1234-5678", "테스트 주소");
        Member savedMember = memberRepository.save(member);

        // when
        Optional<Member> foundMember = memberRepository.findById(savedMember.getId());

        // then
        assertThat(foundMember).isPresent();
        assertThat(foundMember.get().getName()).isEqualTo("테스트회원");
        assertThat(foundMember.get().getEmail()).isEqualTo("test@example.com");
        assertThat(foundMember.get().getPhone()).isEqualTo("010-1234-5678");
        assertThat(foundMember.get().getAddress()).isEqualTo("테스트 주소");
    }

    @Test
    @DisplayName("회원 삭제")
    void delete_Member_Success() {
        // given
        Member member = createMember("삭제테스트", "delete@example.com", "010-0000-0000", "삭제주소");
        Member savedMember = memberRepository.save(member);
        Long memberId = savedMember.getId();

        // when
        memberRepository.deleteById(memberId);

        // then
        Optional<Member> deletedMember = memberRepository.findById(memberId);
        assertThat(deletedMember).isEmpty();
    }

    @Test
    @DisplayName("회원 수 조회")
    void count_ReturnsCorrectNumber() {
        // given
        Member member1 = createMember("회원1", "c1@example.com", "010-1111-1111", "주소1");
        Member member2 = createMember("회원2", "c2@example.com", "010-2222-2222", "주소2");

        memberRepository.save(member1);
        memberRepository.save(member2);

        // when
        long count = memberRepository.count();

        // then
        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("ID로 회원 존재 여부 확인")
    void existsById_ExistingMember_ReturnsTrue() {
        // given
        Member member = createMember("존재테스트", "exist@example.com", "010-0000-0000", "주소");
        Member savedMember = memberRepository.save(member);

        // when
        boolean exists = memberRepository.existsById(savedMember.getId());

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("존재하지 않는 ID로 확인 시 false 반환")
    void existsById_NonExistingMember_ReturnsFalse() {
        // when
        boolean exists = memberRepository.existsById(999999L);

        // then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("이메일로 회원 존재 여부 확인")
    void existsByEmail_ExistingEmail_ReturnsTrue() {
        // given
        Member member = createMember("이메일테스트", "emailtest@example.com", "010-1234-5678", "주소");
        memberRepository.save(member);

        // when
        boolean exists = memberRepository.existsByEmail("emailtest@example.com");

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("존재하지 않는 이메일로 확인 시 false 반환")
    void existsByEmail_NonExistingEmail_ReturnsFalse() {
        // when
        boolean exists = memberRepository.existsByEmail("notexist@example.com");

        // then
        assertThat(exists).isFalse();
    }

    private Member createMember(String name, String email, String phone, String address) {
        return Member.builder()
                .name(name)
                .email(email)
                .phone(phone)
                .address(address)
                .build();
    }
}