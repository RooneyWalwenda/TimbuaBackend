package com.Timbua.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "materials")
public class Material {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String category;
    private double price;
    private String currency;
    private String unit;
    private String location;
    private double rating;
    private String contact;
    private String deliveryTime;
    private Integer minOrder;
    private boolean available;

    private Double supplierLat;
    private Double supplierLng;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    @JsonIgnore  // This prevents circular serialization
    private Supplier supplier;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Constructors
    public Material() {}

    public Material(String name, String category, double price, String unit, boolean available) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.unit = unit;
        this.available = available;
    }

    // Getters and setters
    public Long getId() { 
        return id; 
    }
    
    public void setId(Long id) { 
        this.id = id; 
    }

    public String getName() { 
        return name; 
    }
    
    public void setName(String name) { 
        this.name = name; 
    }

    public String getCategory() { 
        return category; 
    }
    
    public void setCategory(String category) { 
        this.category = category; 
    }

    public double getPrice() { 
        return price; 
    }
    
    public void setPrice(double price) { 
        this.price = price; 
    }

    public String getCurrency() { 
        return currency; 
    }
    
    public void setCurrency(String currency) { 
        this.currency = currency; 
    }

    public String getUnit() { 
        return unit; 
    }
    
    public void setUnit(String unit) { 
        this.unit = unit; 
    }

    public String getLocation() { 
        return location; 
    }
    
    public void setLocation(String location) { 
        this.location = location; 
    }

    public double getRating() { 
        return rating; 
    }
    
    public void setRating(double rating) { 
        this.rating = rating; 
    }

    public String getContact() { 
        return contact; 
    }
    
    public void setContact(String contact) { 
        this.contact = contact; 
    }

    public String getDeliveryTime() { 
        return deliveryTime; 
    }
    
    public void setDeliveryTime(String deliveryTime) { 
        this.deliveryTime = deliveryTime; 
    }

    public Integer getMinOrder() { 
        return minOrder; 
    }
    
    public void setMinOrder(Integer minOrder) { 
        this.minOrder = minOrder; 
    }

    public boolean isAvailable() { 
        return available; 
    }
    
    public void setAvailable(boolean available) { 
        this.available = available; 
    }

    public Double getSupplierLat() { 
        return supplierLat; 
    }
    
    public void setSupplierLat(Double supplierLat) { 
        this.supplierLat = supplierLat; 
    }

    public Double getSupplierLng() { 
        return supplierLng; 
    }
    
    public void setSupplierLng(Double supplierLng) { 
        this.supplierLng = supplierLng; 
    }

    public Supplier getSupplier() { 
        return supplier; 
    }
    
    public void setSupplier(Supplier supplier) { 
        this.supplier = supplier; 
    }

    public LocalDateTime getCreatedAt() { 
        return createdAt; 
    }
    
    public void setCreatedAt(LocalDateTime createdAt) { 
        this.createdAt = createdAt; 
    }

    public LocalDateTime getUpdatedAt() { 
        return updatedAt; 
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) { 
        this.updatedAt = updatedAt; 
    }

    // toString method for debugging
    @Override
    public String toString() {
        return "Material{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", price=" + price +
                ", currency='" + currency + '\'' +
                ", unit='" + unit + '\'' +
                ", available=" + available +
                '}';
    }
}
