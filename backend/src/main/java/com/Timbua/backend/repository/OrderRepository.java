package com.Timbua.backend.repository;

import com.Timbua.backend.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findBySupplierId(Long supplierId);

    List<Order> findByContractorId(Long contractorId);

    List<Order> findBySiteId(Long siteId);

    List<Order> findByPaymentStatus(Order.PaymentStatus paymentStatus);

    List<Order> findByStatus(Order.Status status);

    List<Order> findByOrderDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT o FROM Order o WHERE o.quotationRequest.id = :requestId")
    List<Order> findByQuotationRequestId(@Param("requestId") Long requestId);

    @Query("SELECT o FROM Order o WHERE o.orderReference = :orderReference")
    Order findByOrderReference(@Param("orderReference") String orderReference);

    @Query("SELECT o FROM Order o WHERE o.supplier.id = :supplierId AND o.status = :status")
    List<Order> findBySupplierIdAndStatus(@Param("supplierId") Long supplierId,
                                          @Param("status") Order.Status status);
}
