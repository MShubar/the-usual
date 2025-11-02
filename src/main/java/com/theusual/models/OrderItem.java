package com.theusual.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.util.Map;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem implements Serializable {
    
    @Column(name = "item_id")
    private Long itemId;
    
    @Column(name = "name")
    private String name;
    
    @Column(name = "image")
    private String image;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "quantity")
    private Integer quantity;
    
    @Column(name = "price")
    private Double price;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "customizations", columnDefinition = "jsonb")
    private Map<String, Object> customizations; // JSON string for size, milk, shots, etc.
}
