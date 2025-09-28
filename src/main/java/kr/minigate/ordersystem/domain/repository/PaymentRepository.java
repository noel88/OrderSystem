package kr.minigate.ordersystem.domain.repository;

import kr.minigate.ordersystem.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrderId(Long orderId);
    List<Payment> findAllByOrderByCreatedAtDesc();
}