package kr.minigate.ordersystem.application.service;

import kr.minigate.ordersystem.application.dto.MemberQuery;

import java.util.List;

public interface MemberQueryService {
    List<MemberQuery> getAllMembers();
    MemberQuery getMember(Long id);
}