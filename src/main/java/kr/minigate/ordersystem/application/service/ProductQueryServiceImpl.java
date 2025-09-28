package kr.minigate.ordersystem.application.service;

import kr.minigate.ordersystem.application.dto.ProductQuery;
import kr.minigate.ordersystem.domain.Product;
import kr.minigate.ordersystem.domain.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProductQueryServiceImpl implements ProductQueryService {

    private final ProductRepository productRepository;

    public ProductQueryServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<ProductQuery> getAllProducts() {
        return productRepository.findAll()
            .stream()
            .map(ProductQuery::from)
            .toList();
    }

    @Override
    public ProductQuery getProduct(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다"));
        return ProductQuery.from(product);
    }
}