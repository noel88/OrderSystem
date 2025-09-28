package kr.minigate.ordersystem.application.service;

import kr.minigate.ordersystem.application.dto.ProductQuery;
import kr.minigate.ordersystem.domain.Product;
import kr.minigate.ordersystem.domain.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ProductQueryServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductQueryServiceImpl productQueryService;

    @Test
    void 모든_상품_조회_성공() {
        // given
        List<Product> products = List.of(
            Product.builder().name("아이폰 15").description("애플 스마트폰").price(new BigDecimal("1200000")).stock(10).build(),
            Product.builder().name("갤럭시 S24").description("삼성 스마트폰").price(new BigDecimal("1100000")).stock(5).build()
        );

        given(productRepository.findAll()).willReturn(products);

        // when
        List<ProductQuery> result = productQueryService.getAllProducts();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).name()).isEqualTo("아이폰 15");
        assertThat(result.get(1).name()).isEqualTo("갤럭시 S24");

        then(productRepository).should().findAll();
    }

    @Test
    void 상품_조회_성공() {
        // given
        Long productId = 1L;
        Product product = Product.builder()
            .name("아이폰 15")
            .description("애플 스마트폰")
            .price(new BigDecimal("1200000"))
            .stock(10)
            .build();

        given(productRepository.findById(productId)).willReturn(Optional.of(product));

        // when
        ProductQuery result = productQueryService.getProduct(productId);

        // then
        assertThat(result.name()).isEqualTo("아이폰 15");
        assertThat(result.description()).isEqualTo("애플 스마트폰");
        assertThat(result.price()).isEqualByComparingTo(new BigDecimal("1200000"));
        assertThat(result.stock()).isEqualTo(10);

        then(productRepository).should().findById(productId);
    }

    @Test
    void 상품_조회_실패_존재하지_않는_상품() {
        // given
        Long productId = 999L;

        given(productRepository.findById(productId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> productQueryService.getProduct(productId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("존재하지 않는 상품입니다");

        then(productRepository).should().findById(productId);
    }
}