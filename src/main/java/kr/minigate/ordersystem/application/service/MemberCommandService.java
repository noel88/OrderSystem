package kr.minigate.ordersystem.application.service;

import kr.minigate.ordersystem.application.dto.MemberCreateCommand;
import kr.minigate.ordersystem.application.dto.MemberQuery;

public interface MemberCommandService {
    MemberQuery createMember(MemberCreateCommand command);
}