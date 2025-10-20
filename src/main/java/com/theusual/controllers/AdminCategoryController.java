package com.theusual.controllers;

import com.theusual.models.Category;
import com.theusual.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/admin/categories")
@CrossOrigin(origins = "${app.client-origin}")
public class AdminCategoryController {
    private final CategoryService categoryService;

    public AdminCategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<?> listAllCategories() {
        try {
            return ResponseEntity.ok(categoryService.listAll());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> createCategory(
            @RequestParam("name") String name,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        try {
            System.out.println("Creating category with name: " + name);
            System.out.println("File received: " + (file != null ? file.getOriginalFilename() : "null"));

            Category category = categoryService.create(name, file);
            return ResponseEntity.ok(category);
        } catch (Exception e) {
            e.printStackTrace(); // This will print the full stack trace to console
            return ResponseEntity.internalServerError().body("Error creating category: " + e.getMessage());
        }
    }



    @PutMapping("/{id}")
    public Category updateCategory(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam(required = false) MultipartFile image) throws IOException {
        // Call the service with correct arguments
        return categoryService.update(id, name, image);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
