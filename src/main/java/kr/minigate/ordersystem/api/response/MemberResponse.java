package kr.minigate.ordersystem.api.response;

import kr.minigate.ordersystem.application.dto.MemberQuery;
import kr.minigate.ordersystem.domain.Member;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MemberResponse {

    private final Long id;
    private final String name;
    private final String email;
    private final String phone;
    private final String address;
    private final LocalDateTime createdAt;

    public MemberResponse(Member member) {
        this.id = member.getId();
        this.name = member.getName();
        this.email = member.getEmail();
        this.phone = member.getPhone();
        this.address = member.getAddress();
        this.createdAt = member.getCreatedAt();
    }

    public MemberResponse(MemberQuery memberQuery) {
        this.id = memberQuery.id();
        this.name = memberQuery.name();
        this.email = memberQuery.email();
        this.phone = memberQuery.phone();
        this.address = memberQuery.address();
        this.createdAt = memberQuery.createdAt();
    }

    public MemberResponse(Long id, String name, String email, String phone, String address, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.createdAt = createdAt;
    }
}