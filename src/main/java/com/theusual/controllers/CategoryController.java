package com.theusual.controllers;

import com.theusual.dto.ProductResponseDTO;
import com.theusual.models.Category;
import com.theusual.models.Product;
import com.theusual.models.Subcategory;
import com.theusual.repositories.CategoryRepository;
import com.theusual.repositories.ProductRepository;
import com.theusual.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.io.IOException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryRepository categoryRepository;
    private final ProductRepository itemRepository;

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<?> list() {
        try {
            return ResponseEntity.ok(categoryService.listAll());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{idOrName}/sub")
    @Transactional(readOnly = true)
    public ResponseEntity<?> getSubCategoriesByIdOrName(@PathVariable String idOrName) {
        try {
            // Try to parse as Long first (ID)
            try {
                Long id = Long.parseLong(idOrName);
                List<Subcategory> subs = categoryService.getSubCategories(id);
                return ResponseEntity.ok(subs);
            } catch (NumberFormatException e) {
                // If not a number, treat as category name
                Optional<Category> categoryOpt = categoryRepository.findByNameIgnoreCase(idOrName);
                if (categoryOpt.isEmpty()) {
                    return ResponseEntity.notFound().build();
                }
                return ResponseEntity.ok(categoryOpt.get().getSubcategories());
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/name/{categoryName}/sub")
    @Transactional(readOnly = true)
    public ResponseEntity<?> getSubCategoriesByName(@PathVariable String categoryName) {
        try {
            Optional<Category> categoryOpt = categoryRepository.findByNameIgnoreCase(categoryName);
            if (categoryOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(categoryOpt.get().getSubcategories());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    private List<String> parseOptionString(String option) {
        if (option == null || option.trim().length() < 2) return new ArrayList<>();
        String cleaned = option.replaceAll("[{}\"]", "");
        if (cleaned.isBlank()) return new ArrayList<>();
        return Arrays.stream(cleaned.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }
    @GetMapping("/{categoryIdOrName}/{subName}/items")
    @Transactional(readOnly = true)
    public ResponseEntity<List<ProductResponseDTO>> getItemsBySubCategory(
            @PathVariable String categoryIdOrName,
            @PathVariable String subName) {

        Optional<Category> categoryOpt;

        // Try to parse as Long first (ID)
        try {
            Long categoryId = Long.parseLong(categoryIdOrName);
            categoryOpt = categoryRepository.findById(categoryId);
        } catch (NumberFormatException e) {
            // If not a number, treat as category name
            categoryOpt = categoryRepository.findByNameIgnoreCase(categoryIdOrName);
        }

        if (categoryOpt.isEmpty()) return ResponseEntity.notFound().build();

        Optional<Subcategory> subOpt = categoryOpt.get().getSubcategories()
                .stream()
                .filter(sub -> sub.getName().equalsIgnoreCase(subName))
                .findFirst();

        if (subOpt.isEmpty()) return ResponseEntity.notFound().build();

        List<Product> items = itemRepository.findBySubcategory_Id(subOpt.get().getId());

        // ...existing code...
        List<ProductResponseDTO> response = items.stream().map(product -> {
            ProductResponseDTO dto = new ProductResponseDTO();
            dto.setId(product.getId());
            dto.setName(product.getName());
            dto.setDescription(product.getDescription());
            dto.setPrice(product.getPrice());
            dto.setImage(product.getImage());

            dto.setSizes(parseOptionString(product.getSizes()).stream().map(s -> {
                ProductResponseDTO.OptionDTO opt = new ProductResponseDTO.OptionDTO();
                opt.setKey(s.toUpperCase().replace(" ", "_"));
                opt.setValue(s);
                return opt;
            }).collect(Collectors.toList()));

            dto.setMilks(parseOptionString(product.getMilks()).stream().map(m -> {
                ProductResponseDTO.OptionDTO opt = new ProductResponseDTO.OptionDTO();
                opt.setKey(m.toUpperCase().replace(" ", "_"));
                opt.setValue(m);
                return opt;
            }).collect(Collectors.toList()));

            dto.setShots(parseOptionString(product.getShots()).stream().map(sh -> {
                ProductResponseDTO.OptionDTO opt = new ProductResponseDTO.OptionDTO();
                opt.setKey(sh.toUpperCase().replace(" ", "_"));
                opt.setValue(sh);
                return opt;
            }).collect(Collectors.toList()));

            dto.setMixers(parseOptionString(product.getMixers()).stream().map(mx -> {
                ProductResponseDTO.OptionDTO opt = new ProductResponseDTO.OptionDTO();
                opt.setKey(mx.toUpperCase().replace(" ", "_"));
                opt.setValue(mx);
                return opt;
            }).collect(Collectors.toList()));

            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }



    @PostMapping
    public ResponseEntity<?> create(
            @RequestParam("name") String name,
            @RequestParam("file") MultipartFile file) {
        try {
            Category category = categoryService.create(name, file);
            return ResponseEntity.ok(category);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity<?> updateCategory(
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam("file") MultipartFile file) throws IOException {

        categoryService.update(id, name, file);
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            categoryService.delete(id);
            return ResponseEntity.ok(Map.of("ok", true));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}
