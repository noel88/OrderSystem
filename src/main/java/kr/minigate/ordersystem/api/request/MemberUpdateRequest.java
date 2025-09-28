package kr.minigate.ordersystem.api.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class MemberUpdateRequest {

    @NotBlank(message = "이름은 필수입니다")
    private String name;

    private String phone;

    private String address;

    public MemberUpdateRequest() {}

    public MemberUpdateRequest(String name, String phone, String address) {
        this.name = name;
        this.phone = phone;
        this.address = address;
    }
}