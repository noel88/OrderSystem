package kr.minigate.ordersystem.application.service;

import kr.minigate.ordersystem.application.dto.ProductCreateCommand;
import kr.minigate.ordersystem.application.dto.ProductQuery;
import kr.minigate.ordersystem.domain.Product;
import kr.minigate.ordersystem.domain.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ProductCommandServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductCommandServiceImpl productCommandService;

    @Test
    void 상품생성_성공() {
        // given
        ProductCreateCommand command = new ProductCreateCommand(
            "아이폰 15", "애플 스마트폰", new BigDecimal("1200000"), 10
        );

        Product savedProduct = Product.builder()
            .name(command.name())
            .description(command.description())
            .price(command.price())
            .stock(command.stock())
            .build();

        given(productRepository.save(any(Product.class))).willReturn(savedProduct);

        // when
        ProductQuery result = productCommandService.createProduct(command);

        // then
        assertThat(result.name()).isEqualTo("아이폰 15");
        assertThat(result.description()).isEqualTo("애플 스마트폰");
        assertThat(result.price()).isEqualByComparingTo(new BigDecimal("1200000"));
        assertThat(result.stock()).isEqualTo(10);

        then(productRepository).should().save(any(Product.class));
    }
}