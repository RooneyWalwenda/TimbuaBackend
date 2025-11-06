package com.Timbua.backend.repository;

import com.Timbua.backend.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findBySupplierId(Long supplierId);

    List<Order> findBySiteId(Long siteId);

    List<Order> findByPaymentStatus(Order.PaymentStatus paymentStatus);
}
