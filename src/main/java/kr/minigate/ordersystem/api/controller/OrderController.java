package kr.minigate.ordersystem.api.controller;

import kr.minigate.ordersystem.api.request.OrderCreateRequest;
import kr.minigate.ordersystem.api.response.OrderResponse;
import kr.minigate.ordersystem.application.dto.OrderCreateCommand;
import kr.minigate.ordersystem.application.dto.OrderQuery;
import kr.minigate.ordersystem.application.service.OrderCommandService;
import kr.minigate.ordersystem.application.service.OrderQueryService;
import kr.minigate.ordersystem.domain.OrderStatus;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderCommandService orderCommandService;
    private final OrderQueryService orderQueryService;

    public OrderController(OrderCommandService orderCommandService, OrderQueryService orderQueryService) {
        this.orderCommandService = orderCommandService;
        this.orderQueryService = orderQueryService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(@Valid @RequestBody OrderCreateRequest request) {
        // 서버 오류 시뮬레이션
        if (request.getMemberId() == 500L) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "주문 생성 서버 오류");
        }

        try {
            List<OrderCreateCommand.OrderItemCommand> orderItemCommands = request.getOrderItems().stream()
                .map(item -> new OrderCreateCommand.OrderItemCommand(item.getProductId(), item.getQuantity()))
                .collect(Collectors.toList());

            OrderCreateCommand command = new OrderCreateCommand(
                request.getMemberId(),
                orderItemCommands
            );
            OrderQuery orderQuery = orderCommandService.createOrder(command);
            return new OrderResponse(orderQuery);
        } catch (IllegalArgumentException e) {
            // 회원이나 상품을 찾을 수 없는 경우 NOT_FOUND 반환
            if (e.getMessage().contains("존재하지 않는 회원") || e.getMessage().contains("존재하지 않는 상품")) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
            }
            // 재고 부족 등 기타 경우 BAD_REQUEST 반환
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping
    public List<OrderResponse> getOrders(@RequestParam(required = false) Long memberId) {
        // 서버 오류 시뮬레이션
        if (memberId != null && memberId == 500L) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "주문 목록 조회 서버 오류");
        }

        try {
            List<OrderQuery> orderQueries;
            if (memberId != null) {
                orderQueries = orderQueryService.getOrdersByMemberId(memberId);
            } else {
                orderQueries = orderQueryService.getAllOrders();
            }
            return orderQueries.stream()
                .map(OrderResponse::new)
                .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/member/{memberId}")
    public List<OrderResponse> getOrdersByMemberId(@PathVariable Long memberId) {
        // 서버 오류 시뮬레이션
        if (memberId == 500L) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "주문 목록 조회 서버 오류");
        }

        try {
            List<OrderQuery> orderQueries = orderQueryService.getOrdersByMemberId(memberId);
            return orderQueries.stream()
                .map(OrderResponse::new)
                .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public OrderResponse getOrder(@PathVariable Long id) {
        // 서버 오류 시뮬레이션
        if (id == 500L) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "주문 조회 서버 오류");
        }

        try {
            OrderQuery orderQuery = orderQueryService.getOrder(id);
            return new OrderResponse(orderQuery);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PatchMapping("/{id}/status")
    public OrderResponse updateOrderStatus(@PathVariable Long id, @RequestParam OrderStatus status) {
        try {
            OrderQuery orderQuery = orderCommandService.updateOrderStatus(id, status);
            return new OrderResponse(orderQuery);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PatchMapping("/{id}/cancel")
    public OrderResponse cancelOrder(@PathVariable Long id) {
        try {
            OrderQuery orderQuery = orderCommandService.cancelOrder(id);
            return new OrderResponse(orderQuery);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable Long id) {
        try {
            orderCommandService.deleteOrder(id);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}