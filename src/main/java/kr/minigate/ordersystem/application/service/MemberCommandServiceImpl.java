package kr.minigate.ordersystem.application.service;

import kr.minigate.ordersystem.application.dto.MemberCreateCommand;
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
        if (memberRepository.existsByEmail(command.email())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다");
        }

        Member member = Member.builder()
            .name(command.name())
            .email(command.email())
            .phone(command.phone())
            .address(command.address())
            .build();

        Member savedMember = memberRepository.save(member);
        return MemberQuery.from(savedMember);
    }
}