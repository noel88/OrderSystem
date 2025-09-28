package kr.minigate.ordersystem.domain.repository;

import kr.minigate.ordersystem.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByMemberIdOrderByCreatedAtDesc(Long memberId);
}