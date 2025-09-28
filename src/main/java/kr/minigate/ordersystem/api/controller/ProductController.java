package kr.minigate.ordersystem.api.controller;

import kr.minigate.ordersystem.api.request.ProductCreateRequest;
import kr.minigate.ordersystem.api.response.ProductResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse createProduct(@Valid @RequestBody ProductCreateRequest request) {
        // 서버 오류 시뮬레이션
        if ("서버오류상품".equals(request.getName())) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "상품 등록 서버 오류");
        }

        // TODO: Service 계층 연결
        return new ProductResponse(1L, request.getName(), request.getDescription(),
                                 request.getPrice(), request.getStock());
    }

    @GetMapping
    public List<ProductResponse> getAllProducts() {
        // TODO: Service 계층 연결
        return List.of(
            new ProductResponse(1L, "아이폰 15", "애플 스마트폰", new BigDecimal("1200000"), 10),
            new ProductResponse(2L, "갤럭시 S24", "삼성 스마트폰", new BigDecimal("1100000"), 5)
        );
    }

    @GetMapping("/{id}")
    public ProductResponse getProduct(@PathVariable Long id) {
        // 존재하지 않는 상품
        if (id == 999L) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "존재하지 않는 상품입니다");
        }

        // 서버 오류 시뮬레이션
        if (id == 500L) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "상품 조회 서버 오류");
        }

        // TODO: Service 계층 연결
        return new ProductResponse(id, "아이폰 15", "애플 스마트폰", new BigDecimal("1200000"), 10);
    }
}