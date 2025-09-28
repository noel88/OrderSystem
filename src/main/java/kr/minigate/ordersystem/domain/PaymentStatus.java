package kr.minigate.ordersystem.domain;

public enum PaymentStatus {
    COMPLETED("결제완료"),
    FAILED("결제실패"),
    CANCELLED("결제취소"),
    REFUNDED("환불완료");

    private final String description;

    PaymentStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}