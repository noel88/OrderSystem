package kr.minigate.ordersystem.domain.repository;

import kr.minigate.ordersystem.domain.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.yml")
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
    }

    @Test
    @DisplayName("상품 저장 성공")
    void save_Product_Success() {
        // given
        Product product = createProduct("노트북", "고성능 노트북", new BigDecimal("1500000"), 10);

        // when
        Product savedProduct = productRepository.save(product);

        // then
        assertThat(savedProduct).isNotNull();
        assertThat(savedProduct.getId()).isNotNull();
        assertThat(savedProduct.getName()).isEqualTo("노트북");
        assertThat(savedProduct.getDescription()).isEqualTo("고성능 노트북");
        assertThat(savedProduct.getPrice()).isEqualByComparingTo(new BigDecimal("1500000"));
        assertThat(savedProduct.getStock()).isEqualTo(10);
    }

    @Test
    @DisplayName("ID로 상품 조회")
    void findById_ExistingProduct_ReturnsProduct() {
        // given
        Product product = createProduct("마우스", "무선 마우스", new BigDecimal("30000"), 50);
        Product savedProduct = productRepository.save(product);

        // when
        Optional<Product> foundProduct = productRepository.findById(savedProduct.getId());

        // then
        assertThat(foundProduct).isPresent();
        assertThat(foundProduct.get().getName()).isEqualTo("마우스");
        assertThat(foundProduct.get().getPrice()).isEqualByComparingTo(new BigDecimal("30000"));
    }

    @Test
    @DisplayName("존재하지 않는 ID로 조회 시 빈 값 반환")
    void findById_NonExistingProduct_ReturnsEmpty() {
        // when
        Optional<Product> foundProduct = productRepository.findById(999999L);

        // then
        assertThat(foundProduct).isEmpty();
    }

    @Test
    @DisplayName("전체 상품 조회")
    void findAll_ReturnsAllProducts() {
        // given
        Product product1 = createProduct("상품1", "설명1", new BigDecimal("10000"), 10);
        Product product2 = createProduct("상품2", "설명2", new BigDecimal("20000"), 20);
        Product product3 = createProduct("상품3", "설명3", new BigDecimal("30000"), 30);

        productRepository.save(product1);
        productRepository.save(product2);
        productRepository.save(product3);

        // when
        List<Product> products = productRepository.findAll();

        // then
        assertThat(products).hasSize(3);
    }

    @Test
    @DisplayName("상품 생성 후 조회")
    void createAndFind_Product_Success() {
        // given
        Product product = createProduct("상품 테스트", "상품 설명", new BigDecimal("10000"), 10);
        Product savedProduct = productRepository.save(product);

        // when
        Optional<Product> foundProduct = productRepository.findById(savedProduct.getId());

        // then
        assertThat(foundProduct).isPresent();
        assertThat(foundProduct.get().getName()).isEqualTo("상품 테스트");
        assertThat(foundProduct.get().getDescription()).isEqualTo("상품 설명");
        assertThat(foundProduct.get().getPrice()).isEqualByComparingTo(new BigDecimal("10000"));
        assertThat(foundProduct.get().getStock()).isEqualTo(10);
    }

    @Test
    @DisplayName("높은 재고로 상품 생성")
    void createProduct_WithHighStock_Success() {
        // given
        Product product = createProduct("재고 테스트", "설명", new BigDecimal("10000"), 50);

        // when
        Product savedProduct = productRepository.save(product);

        // then
        assertThat(savedProduct.getStock()).isEqualTo(50);
        assertThat(savedProduct.getName()).isEqualTo("재고 테스트");
    }

    @Test
    @DisplayName("낮은 재고로 상품 생성")
    void createProduct_WithLowStock_Success() {
        // given
        Product product = createProduct("재고 테스트", "설명", new BigDecimal("10000"), 5);

        // when
        Product savedProduct = productRepository.save(product);

        // then
        assertThat(savedProduct.getStock()).isEqualTo(5);
        assertThat(savedProduct.getPrice()).isEqualByComparingTo(new BigDecimal("10000"));
    }

    @Test
    @DisplayName("상품 삭제")
    void delete_Product_Success() {
        // given
        Product product = createProduct("삭제 테스트", "설명", new BigDecimal("10000"), 10);
        Product savedProduct = productRepository.save(product);
        Long productId = savedProduct.getId();

        // when
        productRepository.deleteById(productId);

        // then
        Optional<Product> deletedProduct = productRepository.findById(productId);
        assertThat(deletedProduct).isEmpty();
    }

    @Test
    @DisplayName("상품 수 조회")
    void count_ReturnsCorrectNumber() {
        // given
        Product product1 = createProduct("상품1", "설명1", new BigDecimal("10000"), 10);
        Product product2 = createProduct("상품2", "설명2", new BigDecimal("20000"), 20);

        productRepository.save(product1);
        productRepository.save(product2);

        // when
        long count = productRepository.count();

        // then
        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("ID로 상품 존재 여부 확인")
    void existsById_ExistingProduct_ReturnsTrue() {
        // given
        Product product = createProduct("존재 테스트", "설명", new BigDecimal("10000"), 10);
        Product savedProduct = productRepository.save(product);

        // when
        boolean exists = productRepository.existsById(savedProduct.getId());

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("존재하지 않는 ID로 확인 시 false 반환")
    void existsById_NonExistingProduct_ReturnsFalse() {
        // when
        boolean exists = productRepository.existsById(999999L);

        // then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("재고 수량 확인")
    void checkStock_Level_Success() {
        // given
        Product product = createProduct("재고 확인", "설명", new BigDecimal("10000"), 50);
        Product savedProduct = productRepository.save(product);

        // when
        Optional<Product> foundProduct = productRepository.findById(savedProduct.getId());

        // then
        assertThat(foundProduct).isPresent();
        assertThat(foundProduct.get().getStock()).isEqualTo(50);
        assertThat(foundProduct.get().getStock()).isGreaterThan(30);
    }

    @Test
    @DisplayName("재고 비교 테스트")
    void compareStock_Level_Success() {
        // given
        Product product = createProduct("재고 확인", "설명", new BigDecimal("10000"), 20);
        Product savedProduct = productRepository.save(product);

        // when
        Optional<Product> foundProduct = productRepository.findById(savedProduct.getId());

        // then
        assertThat(foundProduct).isPresent();
        assertThat(foundProduct.get().getStock()).isEqualTo(20);
        assertThat(foundProduct.get().getStock()).isLessThan(30);
    }

    private Product createProduct(String name, String description, BigDecimal price, Integer stock) {
        return Product.builder()
                .name(name)
                .description(description)
                .price(price)
                .stock(stock)
                .build();
    }
}