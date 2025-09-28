package kr.minigate.ordersystem.api.request;

import jakarta.validation.constraints.NotNull;
import kr.minigate.ordersystem.domain.PaymentMethod;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentCreateRequest {

    @NotNull(message = "주문 ID는 필수입니다")
    private Long orderId;

    @NotNull(message = "결제 방법은 필수입니다")
    private PaymentMethod paymentMethod;

    public PaymentCreateRequest(Long orderId, PaymentMethod paymentMethod) {
        this.orderId = orderId;
        this.paymentMethod = paymentMethod;
    }
}