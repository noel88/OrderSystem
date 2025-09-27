package kr.minigate.ordersystem.application.dto;

public record MemberCreateCommand(
    String name,
    String email,
    String phone,
    String address
) {
}