package com.theusual.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "items")
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private double price;
    private String image;

    @ManyToOne
    private Subcategory subcategory;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    private String sizes;
    private String milks;
    private String shots;
    private String mixers;

}

