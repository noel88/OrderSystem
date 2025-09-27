package kr.minigate.ordersystem.domain;

public enum OrderStatus {
    CONFIRMED("주문확정"),
    SHIPPED("배송중"),
    DELIVERED("배송완료"),
    CANCELLED("주문취소");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}