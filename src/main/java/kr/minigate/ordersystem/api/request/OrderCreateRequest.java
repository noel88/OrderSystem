package kr.minigate.ordersystem.api.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class OrderCreateRequest {

    @NotNull(message = "회원 ID는 필수입니다")
    private Long memberId;

    @NotEmpty(message = "주문 상품은 최소 1개 이상이어야 합니다")
    @Valid
    private List<OrderItemRequest> orderItems;

    public OrderCreateRequest(Long memberId, List<OrderItemRequest> orderItems) {
        this.memberId = memberId;
        this.orderItems = orderItems;
    }

    @Getter
    @NoArgsConstructor
    public static class OrderItemRequest {
        @NotNull(message = "상품 ID는 필수입니다")
        private Long productId;

        @NotNull(message = "수량은 필수입니다")
        @Positive(message = "수량은 1개 이상이어야 합니다")
        private Integer quantity;

        public OrderItemRequest(Long productId, Integer quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }
    }
}