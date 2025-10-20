package com.theusual.services;

import com.theusual.models.Category;
import com.theusual.models.Subcategory;
import com.theusual.repositories.CategoryRepository;
import com.theusual.repositories.SubCategoryRepository;
import com.theusual.services.AzureStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final SubCategoryRepository subCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final AzureStorageService azureStorageService;

    @GetMapping("/admin/categories")
    public List<Category> listAll() {
        return categoryRepository.findAll();
    }
    public List<Category> getAllPublic() {
        return categoryRepository.findAll(); // Or filter by `public` flag if you have one
    }
    public List<Subcategory> getSubCategories(Long categoryId) {
        return subCategoryRepository.findByCategoryId(categoryId);
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
}
