package com.Timbua.backend.controller;

import com.Timbua.backend.model.Order;
import com.Timbua.backend.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // === Create Order ===
    @PostMapping("/create")
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        return ResponseEntity.ok(orderService.createOrder(order));
    }

    // === Confirm Payment ===
    @PutMapping("/{id}/confirm-payment")
    public ResponseEntity<?> confirmPayment(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.confirmPayment(id));
    }

    // === Cancel Order ===
    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.cancelOrder(id));
    }

    // === Get All Orders ===
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    // === Get Orders by Supplier ===
    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<List<Order>> getOrdersBySupplier(@PathVariable Long supplierId) {
        return ResponseEntity.ok(orderService.getOrdersBySupplier(supplierId));
    }

    // === Get Orders by Site ===
    @GetMapping("/site/{siteId}")
    public ResponseEntity<List<Order>> getOrdersBySite(@PathVariable Long siteId) {
        return ResponseEntity.ok(orderService.getOrdersBySite(siteId));
    }

    // === Get Pending Payment Orders ===
    @GetMapping("/pending-payments")
    public ResponseEntity<List<Order>> getPendingPayments() {
        return ResponseEntity.ok(orderService.getPendingPayments());
    }
}
