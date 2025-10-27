package com.theusual.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String orderNumber;

    @Column(nullable = false)
    private String orderType; // "pickup" or "delivery"

    @Column(nullable = false)
    private Double total;

    @Column(nullable = false)
    private String status; // "pending", "preparing", "ready", "completed"

    @Column(nullable = false)
    private String paymentMethod;

    private String deliveryAddress;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> items;

    @Column(nullable = true)
    private String userId; // phone number

    @Column(nullable = true)
    private Boolean attended = true; // default true, set false if not attended

    @Column(nullable = true)
    private Boolean receiptReceived = false; // true after delivery

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.orderNumber == null) {
            this.orderNumber = String.valueOf(System.currentTimeMillis());
        }
        if (this.status == null) {
            this.status = "pending";
        }
    }
}
