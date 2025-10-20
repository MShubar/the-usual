package com.theusual.controllers;

import com.theusual.models.Subcategory;
import com.theusual.services.SubCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/admin/subcategories")
@RequiredArgsConstructor
public class SubCategoryController {

    private final SubCategoryService subCategoryService;

    @GetMapping("/by-category/{categoryId}")
    public ResponseEntity<?> listByCategory(@PathVariable Long categoryId) {
        try {
            return ResponseEntity.ok(subCategoryService.listByCategory(categoryId));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/sub")
    public ResponseEntity<?> createSubCategory(
            @RequestParam String name,
            @RequestParam String image,
            @RequestParam Long categoryId // <- should be Long if your service expects Long
    ) {
        Subcategory sub = subCategoryService.create(name, image, categoryId);
        return ResponseEntity.ok(sub);
    }

    @PostMapping
    public ResponseEntity<?> createSubcategory(
            @RequestParam("categoryId") Long categoryId,
            @RequestParam("name") String name,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        try {
            System.out.println("Creating subcategory with categoryId: " + categoryId + ", name: " + name);
            String imagePath = (image != null && !image.isEmpty()) ? image.getOriginalFilename() : null;
            Subcategory subcategory = subCategoryService.create(name, imagePath, categoryId);
            return ResponseEntity.ok(subcategory);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @RequestParam(required = false) String name,
                                    @RequestParam(required = false) MultipartFile image) {
        try {
            return ResponseEntity.ok(subCategoryService.update(id, name, image));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            subCategoryService.delete(id);
            return ResponseEntity.ok(Map.of("ok", true));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}

