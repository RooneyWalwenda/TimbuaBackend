package com.Timbua.backend.service;

import com.Timbua.backend.model.Order;
import com.Timbua.backend.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    // Create new order
    public Order createOrder(Order order) {
        order.setOrderReference("ORD-" + System.currentTimeMillis());
        order.setOrderDate(LocalDate.now());
        order.setPaymentStatus(Order.PaymentStatus.PENDING_PAYMENT);
        order.setStatus(Order.Status.ORDERED);
        return orderRepository.save(order);
    }

    // Confirm payment
    public Map<String, Object> confirmPayment(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setPaymentStatus(Order.PaymentStatus.PAID);
        order.setPaymentDate(LocalDateTime.now());
        orderRepository.save(order);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Payment confirmed successfully.");
        response.put("paymentStatus", "PAID");
        response.put("orderReference", order.getOrderReference());
        return response;
    }

    // Cancel order
    public Map<String, Object> cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(Order.Status.CANCELLED);
        order.setPaymentStatus(Order.PaymentStatus.CANCELLED);
        orderRepository.save(order);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Order cancelled successfully.");
        response.put("status", "CANCELLED");
        response.put("orderReference", order.getOrderReference());
        return response;
    }

    // Retrieve all orders
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // Get orders by supplier
    public List<Order> getOrdersBySupplier(Long supplierId) {
        return orderRepository.findBySupplierId(supplierId);
    }

    // Get orders by site
    public List<Order> getOrdersBySite(Long siteId) {
        return orderRepository.findBySiteId(siteId);
    }

    // Get unpaid orders
    public List<Order> getPendingPayments() {
        return orderRepository.findByPaymentStatus(Order.PaymentStatus.PENDING_PAYMENT);
    }
}
