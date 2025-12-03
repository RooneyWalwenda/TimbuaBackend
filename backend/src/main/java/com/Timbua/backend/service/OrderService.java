package com.Timbua.backend.service;

import com.Timbua.backend.model.*;
import com.Timbua.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private QuotationRequestRepository quotationRequestRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private ContractorRepository contractorRepository;

    @Autowired
    private QuoteRepository quoteRepository;

    // Create new order from scratch
    @Transactional
    public Order createOrder(Order order) {
        // Generate order reference
        order.setOrderReference("ORD-" + System.currentTimeMillis());
        order.setOrderDate(LocalDate.now());
        order.setStatus(Order.Status.ORDERED);
        order.setPaymentStatus(Order.PaymentStatus.PENDING_PAYMENT);

        // Calculate total from items if not set
        if (order.getTotalAmount() == null && order.getItems() != null) {
            double total = order.getItems().stream()
                    .mapToDouble(OrderItem::getTotal)
                    .sum();
            order.setTotalAmount(total);
        }

        // Set timestamps
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        return orderRepository.save(order);
    }

    // Create order from accepted quote (Automated workflow)
    @Transactional
    public Order createOrderFromAcceptedQuote(Long quoteId) {
        Quote acceptedQuote = quoteRepository.findById(quoteId)
                .orElseThrow(() -> new RuntimeException("Quote not found"));

        if (acceptedQuote.getStatus() != Quote.Status.ACCEPTED) {
            throw new RuntimeException("Only accepted quotes can be converted to orders");
        }

        QuotationRequest request = acceptedQuote.getQuotationRequest();
        Supplier supplier = acceptedQuote.getSupplier();
        Contractor contractor = request.getContractor();

        // Create new order
        Order order = new Order();
        order.setQuotationRequest(request);
        order.setSupplier(supplier);
        order.setContractor(contractor);
        order.setSiteId(request.getSiteId());
        order.setTotalAmount(acceptedQuote.getTotalAmount());
        order.setOrderReference("ORD-" + System.currentTimeMillis());
        order.setOrderDate(LocalDate.now());
        order.setStatus(Order.Status.ORDERED);
        order.setPaymentStatus(Order.PaymentStatus.PENDING_PAYMENT);

        // Copy delivery terms from quote
        order.setDeliveryInstructions(acceptedQuote.getTerms());

        // Create order items from quotation request
        OrderItem item = new OrderItem();
        item.setMaterialName(request.getMaterial());
        item.setQuantity(request.getQuantity());
        item.setUnit(request.getUnit());
        item.setUnitPrice(acceptedQuote.getTotalAmount() / request.getQuantity());
        item.calculateTotal();

        order.getItems().add(item);

        // Update request status
        request.setStatus(QuotationRequest.Status.ACCEPTED);
        quotationRequestRepository.save(request);

        return orderRepository.save(order);
    }

    // Confirm payment
    @Transactional
    public Map<String, Object> confirmPayment(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getPaymentStatus() == Order.PaymentStatus.PAID) {
            throw new RuntimeException("Order is already paid");
        }

        if (order.getStatus() == Order.Status.CANCELLED) {
            throw new RuntimeException("Cannot confirm payment for cancelled order");
        }

        order.setPaymentStatus(Order.PaymentStatus.PAID);
        order.setPaymentDate(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Payment confirmed successfully.");
        response.put("paymentStatus", "PAID");
        response.put("orderReference", order.getOrderReference());
        response.put("paymentDate", order.getPaymentDate());
        return response;
    }

    // Cancel order
    @Transactional
    public Map<String, Object> cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() == Order.Status.DELIVERED || order.getStatus() == Order.Status.COMPLETED) {
            throw new RuntimeException("Cannot cancel delivered or completed orders");
        }

        order.setStatus(Order.Status.CANCELLED);
        order.setPaymentStatus(Order.PaymentStatus.CANCELLED);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Order cancelled successfully.");
        response.put("status", "CANCELLED");
        response.put("orderReference", order.getOrderReference());
        response.put("cancellationDate", LocalDateTime.now());
        return response;
    }

    // Update order status
    @Transactional
    public Order updateOrderStatus(Long id, Order.Status status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Validate status transition
        validateStatusTransition(order.getStatus(), status);

        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());

        // Handle special statuses
        if (status == Order.Status.DELIVERED) {
            order.setActualDeliveryDate(LocalDate.now());
        }

        return orderRepository.save(order);
    }

    private void validateStatusTransition(Order.Status current, Order.Status next) {
        // Add business rules for status transitions
        if (current == Order.Status.CANCELLED && next != Order.Status.CANCELLED) {
            throw new RuntimeException("Cannot change status of cancelled order");
        }

        if (current == Order.Status.COMPLETED && next != Order.Status.COMPLETED) {
            throw new RuntimeException("Cannot change status of completed order");
        }
    }

    // Retrieve all orders
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // Get orders by supplier
    public List<Order> getOrdersBySupplier(Long supplierId) {
        return orderRepository.findBySupplierId(supplierId);
    }

    // Get orders by contractor
    public List<Order> getOrdersByContractor(Long contractorId) {
        return orderRepository.findByContractorId(contractorId);
    }

    // Get orders by site
    public List<Order> getOrdersBySite(Long siteId) {
        return orderRepository.findBySiteId(siteId);
    }

    // Get unpaid orders
    public List<Order> getPendingPayments() {
        return orderRepository.findByPaymentStatus(Order.PaymentStatus.PENDING_PAYMENT);
    }

    // Get order by ID
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    // Update order
    @Transactional
    public Order updateOrder(Long id, Order orderDetails) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Don't update certain fields
        order.setDeliveryAddress(orderDetails.getDeliveryAddress());
        order.setDeliveryInstructions(orderDetails.getDeliveryInstructions());
        order.setExpectedDeliveryDate(orderDetails.getExpectedDeliveryDate());
        order.setPaymentTerms(orderDetails.getPaymentTerms());
        order.setSpecialRequirements(orderDetails.getSpecialRequirements());
        order.setNotes(orderDetails.getNotes());
        order.setUpdatedAt(LocalDateTime.now());

        return orderRepository.save(order);
    }

    // Add item to order
    @Transactional
    public Order addItemToOrder(Long orderId, OrderItem item) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        item.setOrder(order);
        item.calculateTotal();
        order.getItems().add(item);
        order.recalculateTotal();
        order.setUpdatedAt(LocalDateTime.now());

        return orderRepository.save(order);
    }

    // Remove item from order
    @Transactional
    public Order removeItemFromOrder(Long orderId, Long itemId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.getItems().removeIf(item -> item.getId().equals(itemId));
        order.recalculateTotal();
        order.setUpdatedAt(LocalDateTime.now());

        return orderRepository.save(order);
    }
}
