package kr.minigate.ordersystem.application.service;

import kr.minigate.ordersystem.application.dto.ProductCreateCommand;
import kr.minigate.ordersystem.application.dto.ProductQuery;

public interface ProductCommandService {
    ProductQuery createProduct(ProductCreateCommand command);
}