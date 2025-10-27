package com.theusual.services;

import com.theusual.models.Category;
import com.theusual.models.Subcategory;
import com.theusual.models.Product;
import com.theusual.repositories.CategoryRepository;
import com.theusual.repositories.SubCategoryRepository;
import com.theusual.repositories.ProductRepository;
import com.theusual.services.AzureStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.Cacheable;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final SubCategoryRepository subCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final AzureStorageService azureStorageService;
    private final ProductRepository productRepository;

    @GetMapping("/admin/categories")
    public List<Category> listAll() {
        return categoryRepository.findAll();
    }
    public List<Category> getAllPublic() {
        return categoryRepository.findAll(); // Or filter by `public` flag if you have one
    }
    @Cacheable(value = "subcategories", key = "#categoryId")
    @Transactional(readOnly = true)
    public List<Subcategory> getSubCategories(Long categoryId) {
        long startTime = System.currentTimeMillis();
        List<Subcategory> subcategories = subCategoryRepository.findByCategoryId(categoryId);
        long duration = System.currentTimeMillis() - startTime;
        System.out.println("Database query for subcategories (categoryId=" + categoryId + ") took " + duration + "ms");
        System.out.println("Total subcategories returned: " + subcategories.size());
        return subcategories;
    }


    public Category create(String name, MultipartFile image) throws IOException {
        String imageUrl = image != null ? azureStorageService.uploadFile(image) : null;
        Category c = new Category();
        c.setName(name);
        return categoryRepository.save(c);
    }

    public Category update(Long id, String name, MultipartFile image) throws IOException {
        Category c = categoryRepository.findById(id).orElseThrow();
        if (name != null) c.setName(name);
        return categoryRepository.save(c);
    }

    public void delete(Long id) {
        categoryRepository.deleteById(id);
    }

    @Cacheable("categories")
    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        long startTime = System.currentTimeMillis();
        List<Category> categories = categoryRepository.findAll();
        long duration = System.currentTimeMillis() - startTime;
        System.out.println("Database query for categories took " + duration + "ms");
        System.out.println("Total categories returned: " + categories.size());
        return categories;
    }

    @Cacheable(value = "items", key = "#subcategoryId")
    @Transactional(readOnly = true)
    public List<Product> getItemsBySubcategoryId(Long subcategoryId) {
        long startTime = System.currentTimeMillis();
        List<Product> items = productRepository.findBySubcategory_Id(subcategoryId);
        long duration = System.currentTimeMillis() - startTime;
        System.out.println("Database query for items (subcategoryId=" + subcategoryId + ") took " + duration + "ms");
        System.out.println("Total items returned: " + items.size());
        return items;
    }
}
