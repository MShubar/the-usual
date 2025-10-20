package com.theusual.services;

import com.theusual.models.Subcategory;
import com.theusual.models.Category;
import com.theusual.repositories.SubCategoryRepository;
import com.theusual.repositories.CategoryRepository;
import com.theusual.services.AzureStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubCategoryService {

    private final SubCategoryRepository subCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final AzureStorageService azureStorageService;

    public List<Subcategory> listByCategory(Long categoryId) {
        return subCategoryRepository.findByCategoryId(categoryId);
    }

    public Subcategory create(String name, String image, Long categoryId) {
        // Currently probably wrong constructor here
        // new Subcategory(null, name, image, category); <-- causes compile error

        // ✅ Correct way:
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Subcategory sub = new Subcategory();
        sub.setName(name);
        sub.setImage(image);
        sub.setCategory(category);

        return subCategoryRepository.save(sub);
    }

    public Subcategory update(Long id, String name, MultipartFile image) throws IOException {
        Subcategory s = subCategoryRepository.findById(id).orElseThrow();
        if (name != null) s.setName(name);
        if (image != null) s.setImage(azureStorageService.uploadFile(image));
        return subCategoryRepository.save(s);
    }

    public void delete(Long id) {
        subCategoryRepository.deleteById(id);
    }
}
