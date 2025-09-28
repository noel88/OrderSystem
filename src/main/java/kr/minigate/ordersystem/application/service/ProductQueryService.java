package kr.minigate.ordersystem.application.service;

import kr.minigate.ordersystem.application.dto.ProductQuery;

import java.util.List;

public interface ProductQueryService {
    List<ProductQuery> getAllProducts();
    ProductQuery getProduct(Long id);
}