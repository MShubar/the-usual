package com.theusual.controllers;

import com.theusual.models.Product;
import com.theusual.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<?> listAll() {
        try {
            return ResponseEntity.ok(productService.listAll());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
    @GetMapping("/enums")
    public ResponseEntity<?> getEnums() {
        try {
            // Example logic: return hardcoded enums for frontend
            Map<String, Object> enums = Map.of(
                    "sizes", new String[]{"Small", "Medium", "Large", "XL"},
                    "milks", new String[]{"Regular", "Soy", "Oat", "Almond"},
                    "shots", new String[]{"1 Shot", "2 Shots", "3 Shots"},
                    "mixers", new String[]{"Sprite", "Redbull"}
            );
            return ResponseEntity.ok(enums);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestParam Long subcategoryId,
                                    @RequestParam String name,
                                    @RequestParam(required = false) String description,
                                    @RequestParam double price,
                                    @RequestParam(required = false) MultipartFile image) {
        try {
            Product p = productService.create(subcategoryId, name, description, price, image);
            return ResponseEntity.ok(p);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @RequestParam(required = false) String name,
                                    @RequestParam(required = false) String description,
                                    @RequestParam(required = false) Double price,
                                    @RequestParam(required = false) MultipartFile image) {
        try {
            return ResponseEntity.ok(productService.update(id, name, description, price, image));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            productService.delete(id);
            return ResponseEntity.ok(Map.of("ok", true));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}
