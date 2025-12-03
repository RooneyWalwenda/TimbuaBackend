package com.Timbua.backend.controller;

import com.Timbua.backend.model.Order;
import com.Timbua.backend.model.OrderItem;
import com.Timbua.backend.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
@Tag(name = "Orders", description = "APIs for managing purchase orders created from accepted quotes")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // === Create Order ===
    @PostMapping("/create")
    @Operation(
            summary = "Create a new order",
            description = "Creates a purchase order with items, supplier, contractor, and site details"
    )
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        return ResponseEntity.ok(orderService.createOrder(order));
    }

    // === Create Order from Accepted Quote ===
    @PostMapping("/from-quote/{quoteId}")
    @Operation(
            summary = "Create order from accepted quote",
            description = "Automatically creates an order from an accepted quote (workflow automation)"
    )
    public ResponseEntity<Order> createOrderFromQuote(
            @Parameter(description = "Accepted Quote ID", example = "1", required = true)
            @PathVariable Long quoteId) {
        return ResponseEntity.ok(orderService.createOrderFromAcceptedQuote(quoteId));
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

    // === Update Order Status ===
    @PutMapping("/{id}/status")
    @Operation(summary = "Update order status", description = "Update the status of an order")
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam Order.Status status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, status));
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

    // === Get Orders by Contractor ===
    @GetMapping("/contractor/{contractorId}")
    @Operation(summary = "Get orders by contractor", description = "Get all orders for a specific contractor")
    public ResponseEntity<List<Order>> getOrdersByContractor(@PathVariable Long contractorId) {
        return ResponseEntity.ok(orderService.getOrdersByContractor(contractorId));
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

    // === Get Single Order ===
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    // === Update Order ===
    @PutMapping("/{id}")
    @Operation(summary = "Update order details", description = "Update order information (delivery, notes, etc.)")
    public ResponseEntity<Order> updateOrder(
            @PathVariable Long id,
            @RequestBody Order orderDetails) {
        return ResponseEntity.ok(orderService.updateOrder(id, orderDetails));
    }

    // === Add Item to Order ===
    @PostMapping("/{orderId}/items")
    @Operation(summary = "Add item to order", description = "Add a new item to an existing order")
    public ResponseEntity<Order> addItemToOrder(
            @PathVariable Long orderId,
            @RequestBody OrderItem item) {
        return ResponseEntity.ok(orderService.addItemToOrder(orderId, item));
    }

    // === Remove Item from Order ===
    @DeleteMapping("/{orderId}/items/{itemId}")
    @Operation(summary = "Remove item from order", description = "Remove an item from an order")
    public ResponseEntity<Order> removeItemFromOrder(
            @PathVariable Long orderId,
            @PathVariable Long itemId) {
        return ResponseEntity.ok(orderService.removeItemFromOrder(orderId, itemId));
    }
}
