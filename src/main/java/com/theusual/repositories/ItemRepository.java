package com.theusual.repositories;

import com.theusual.models.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findBySubcategory_NameAndSubcategory_Category_Name(String subcategory, String category);
}