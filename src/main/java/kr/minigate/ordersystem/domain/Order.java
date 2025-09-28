package kr.minigate.ordersystem.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems;

    @Builder
    public Order(Member member, BigDecimal totalAmount) {
        this.member = member;
        this.totalAmount = totalAmount;
        this.status = OrderStatus.CONFIRMED;
    }

    public void updateStatus(OrderStatus status) {
        this.status = status;
    }

    public void cancel() {
        if (this.status == OrderStatus.SHIPPED || this.status == OrderStatus.DELIVERED) {
            throw new IllegalStateException("이미 배송된 주문은 취소할 수 없습니다");
        }
        this.status = OrderStatus.CANCELLED;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public BigDecimal calculateTotalAmount() {
        if (orderItems == null || orderItems.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return orderItems.stream()
            .map(OrderItem::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void addOrderItem(OrderItem orderItem) {
        if (this.orderItems == null) {
            this.orderItems = new java.util.ArrayList<>();
        }
        orderItem.setOrder(this);
        this.orderItems.add(orderItem);
        this.totalAmount = calculateTotalAmount();
    }

    public void addOrderItems(List<OrderItem> orderItems) {
        orderItems.forEach(this::addOrderItem);
    }

    public boolean canCancelPayment() {
        return this.status != OrderStatus.SHIPPED && this.status != OrderStatus.DELIVERED;
    }

    public void restoreStock() {
        if (this.orderItems != null) {
            this.orderItems.forEach(orderItem -> {
                Product product = orderItem.getProduct();
                product.increaseStock(orderItem.getQuantity());
            });
        }
    }

    public static Order createOrder(Member member, List<OrderItem> orderItems) {
        Order order = Order.builder()
            .member(member)
            .totalAmount(BigDecimal.ZERO)
            .build();

        order.addOrderItems(orderItems);
        return order;
    }
}