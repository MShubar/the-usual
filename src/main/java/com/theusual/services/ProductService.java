package com.theusual.services;

import com.theusual.models.Category;
import com.theusual.models.Product;
import com.theusual.models.Subcategory;
import com.theusual.repositories.CategoryRepository;
import com.theusual.repositories.ProductRepository;
import com.theusual.repositories.SubCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final AzureStorageService azureStorageService;


    public List<Product> listAll() {
        return productRepository.findAll();
    }

    public Product create(Long subcategoryId, String name, String description, double price, MultipartFile image) throws IOException {
        Subcategory sub = subCategoryRepository.findById(subcategoryId)
                .orElseThrow(() -> new RuntimeException("Subcategory not found"));

        // Get the category from the subcategory
        Category category = sub.getCategory();
        if (category == null) {
            throw new RuntimeException("Category not found for the subcategory");
        }

        String imageUrl = image != null ? azureStorageService.uploadFile(image) : null;

        // Construct Product with null id and option fields as strings (match entity field types)
        Product product = new Product(
                null,       // id -> let JPA generate
                name,
                description,
                price,
                imageUrl,
                sub,
                category,
                "{}",       // sizes
                "{}",       // milks
                "{}",       // shots
                "{}"        // mixers
        );

        return productRepository.save(product);
    }



    public Product update(Long id, String name, String description, Double price, MultipartFile image) throws IOException {
        Product p = productRepository.findById(id).orElseThrow();
        if (name != null) p.setName(name);
        if (description != null) p.setDescription(description);
        if (price != null) p.setPrice(price);
        if (image != null) p.setImage(azureStorageService.uploadFile(image));
        return productRepository.save(p);
    }


    public void delete(Long id) {
        productRepository.deleteById(id);
    }
}
