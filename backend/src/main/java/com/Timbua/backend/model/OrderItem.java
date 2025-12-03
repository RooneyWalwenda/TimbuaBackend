package com.Timbua.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "order_items")
@JsonIgnoreProperties({"order"})
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private String materialName;
    private String materialCode;
    private String category;

    private Double quantity;
    private String unit;

    private Double unitPrice;
    private Double total;

    private String specifications;
    private String grade;
    private String brand;

    private Double taxRate = 0.0;
    private Double taxAmount = 0.0;
    private Double discount = 0.0;

    // ===== Constructors =====
    public OrderItem() {}

    public OrderItem(String materialName, Double quantity, String unit, Double unitPrice) {
        this.materialName = materialName;
        this.quantity = quantity;
        this.unit = unit;
        this.unitPrice = unitPrice;
        calculateTotal();
    }

    // ===== Helper Methods =====
    public void calculateTotal() {
        double subtotal = quantity * unitPrice;
        double discountAmount = subtotal * (discount / 100);
        double taxableAmount = subtotal - discountAmount;
        double tax = taxableAmount * (taxRate / 100);

        this.total = taxableAmount + tax;
        this.taxAmount = tax;
    }

    // ===== Getters and Setters =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public String getMaterialName() { return materialName; }
    public void setMaterialName(String materialName) { this.materialName = materialName; }

    public String getMaterialCode() { return materialCode; }
    public void setMaterialCode(String materialCode) { this.materialCode = materialCode; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Double getQuantity() { return quantity; }
    public void setQuantity(Double quantity) {
        this.quantity = quantity;
        calculateTotal();
    }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public Double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
        calculateTotal();
    }

    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }

    public String getSpecifications() { return specifications; }
    public void setSpecifications(String specifications) { this.specifications = specifications; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public Double getTaxRate() { return taxRate; }
    public void setTaxRate(Double taxRate) {
        this.taxRate = taxRate;
        calculateTotal();
    }

    public Double getTaxAmount() { return taxAmount; }
    public void setTaxAmount(Double taxAmount) { this.taxAmount = taxAmount; }

    public Double getDiscount() { return discount; }
    public void setDiscount(Double discount) {
        this.discount = discount;
        calculateTotal();
    }
}
