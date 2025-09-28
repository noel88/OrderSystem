package kr.minigate.ordersystem.application.dto;

public record MemberUpdateCommand(
    String name,
    String phone,
    String address
) {
}