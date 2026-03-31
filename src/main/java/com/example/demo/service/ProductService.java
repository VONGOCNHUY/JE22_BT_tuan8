package com.example.demo.service;

import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public void saveProduct(Product product) {
        productRepository.save(product);
    }

    public Product getProductById(int id) {
        return productRepository.findById(Long.valueOf(id)).orElse(null);
    }

    public void deleteProduct(int id) {
        productRepository.deleteById(Long.valueOf(id));
    }

    public List<Product> searchProductsByName(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword);
    }

    public Page<Product> getAllProductsWithPagination(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public Page<Product> searchProductsByNameWithPagination(String keyword, Pageable pageable) {
        return productRepository.findByNameContainingIgnoreCase(keyword, pageable);
    }

    public Page<Product> getProductsByCategoryWithPagination(Long categoryId, Pageable pageable) {
        return productRepository.findByCategoryId(categoryId, pageable);
    }

    public Page<Product> searchProductsByNameAndCategoryWithPagination(String keyword, Long categoryId, Pageable pageable) {
        return productRepository.findByNameContainingIgnoreCaseAndCategoryId(keyword, categoryId, pageable);
    }
}