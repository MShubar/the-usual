package com.theusual.repositories;

import com.theusual.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findBySubcategory_Id(Long subcategoryId);
}