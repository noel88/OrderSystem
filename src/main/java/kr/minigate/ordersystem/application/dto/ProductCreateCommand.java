package kr.minigate.ordersystem.application.dto;

import java.math.BigDecimal;

public record ProductCreateCommand(
    String name,
    String description,
    BigDecimal price,
    Integer stock
) {
}