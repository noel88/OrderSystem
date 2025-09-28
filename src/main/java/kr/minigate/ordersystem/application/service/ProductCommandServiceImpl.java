package kr.minigate.ordersystem.application.service;

import kr.minigate.ordersystem.application.dto.ProductCreateCommand;
import kr.minigate.ordersystem.application.dto.ProductQuery;
import kr.minigate.ordersystem.domain.Product;
import kr.minigate.ordersystem.domain.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductCommandServiceImpl implements ProductCommandService {

    private final ProductRepository productRepository;

    public ProductCommandServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public ProductQuery createProduct(ProductCreateCommand command) {
        Product product = Product.builder()
            .name(command.name())
            .description(command.description())
            .price(command.price())
            .stock(command.stock())
            .build();

        Product savedProduct = productRepository.save(product);
        return ProductQuery.from(savedProduct);
    }
}