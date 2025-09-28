package kr.minigate.ordersystem.application.service;

import kr.minigate.ordersystem.application.dto.MemberCreateCommand;
import kr.minigate.ordersystem.application.dto.MemberUpdateCommand;
import kr.minigate.ordersystem.application.dto.MemberQuery;
import kr.minigate.ordersystem.domain.Member;
import kr.minigate.ordersystem.domain.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MemberCommandServiceImpl implements MemberCommandService {

    private final MemberRepository memberRepository;

    public MemberCommandServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public MemberQuery createMember(MemberCreateCommand command) {
        // 이메일 유효성 검사
        if (command.email() == null || !command.email().contains("@")) {
            throw new IllegalArgumentException("유효하지 않은 이메일 형식입니다");
        }

        // 중복 이메일 확인
        if (memberRepository.existsByEmail(command.email())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다");
        }

        // 필수 필드 검증
        if (command.name() == null || command.name().trim().isEmpty()) {
            throw new IllegalArgumentException("회원 이름은 필수입니다");
        }

        Member member = Member.builder()
            .name(command.name().trim())
            .email(command.email().toLowerCase())
            .phone(command.phone())
            .address(command.address())
            .build();

        Member savedMember = memberRepository.save(member);
        return MemberQuery.from(savedMember);
    }

    @Override
    public MemberQuery updateMember(Long id, MemberUpdateCommand command) {
        Member member = memberRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다"));

        // 업데이트할 데이터 검증
        if (command.name() == null || command.name().trim().isEmpty()) {
            throw new IllegalArgumentException("회원 이름은 필수입니다");
        }

        member.updateProfile(
            command.name().trim(),
            command.phone(),
            command.address()
        );

        return MemberQuery.from(member);
    }

    @Override
    public void deleteMember(Long id) {
        Member member = memberRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다"));

        // 회원 관련 주문이 있는지 확인
        // 실제 구현에서는 OrderRepository를 주입받아 확인해야 함
        // if (orderRepository.existsByMember(member)) {
        //     throw new IllegalStateException("주문 내역이 있는 회원은 삭제할 수 없습니다");
        // }

        memberRepository.delete(member);
    }
}