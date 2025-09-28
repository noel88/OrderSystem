package kr.minigate.ordersystem.application.service;

import kr.minigate.ordersystem.application.dto.MemberQuery;
import kr.minigate.ordersystem.domain.Member;
import kr.minigate.ordersystem.domain.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class MemberQueryServiceImpl implements MemberQueryService {

    private final MemberRepository memberRepository;

    public MemberQueryServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public List<MemberQuery> getAllMembers() {
        return memberRepository.findAll()
            .stream()
            .map(MemberQuery::from)
            .toList();
    }

    @Override
    public MemberQuery getMember(Long id) {
        Member member = memberRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다"));
        return MemberQuery.from(member);
    }
}