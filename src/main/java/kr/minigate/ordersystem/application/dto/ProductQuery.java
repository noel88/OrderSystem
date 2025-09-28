package kr.minigate.ordersystem.application.dto;

import kr.minigate.ordersystem.domain.Product;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductQuery(
    Long id,
    String name,
    String description,
    BigDecimal price,
    Integer stock,
    LocalDateTime createdAt
) {
    public static ProductQuery from(Product product) {
        return new ProductQuery(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getStock(),
            product.getCreatedAt()
        );
    }
}