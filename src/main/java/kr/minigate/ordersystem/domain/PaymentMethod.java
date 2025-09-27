package kr.minigate.ordersystem.domain;

public enum PaymentMethod {
    CARD("신용카드"),
    BANK_TRANSFER("계좌이체"),
    CASH("현금"),
    MOBILE_PAY("간편결제");

    private final String description;

    PaymentMethod(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}