package kr.minigate.ordersystem.application.dto;

import kr.minigate.ordersystem.domain.Member;

import java.time.LocalDateTime;

public record MemberQuery(
    Long id,
    String name,
    String email,
    String phone,
    String address,
    LocalDateTime createdAt
) {
    public static MemberQuery from(Member member) {
        return new MemberQuery(
            member.getId(),
            member.getName(),
            member.getEmail(),
            member.getPhone(),
            member.getAddress(),
            member.getCreatedAt()
        );
    }
}