package com.example.demo.controller;

import com.example.demo.model.Product;
import com.example.demo.service.ProductService;
import com.example.demo.service.CategoryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.validation.BindingResult;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid;

import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {

    private ProductService productService;
    private CategoryService categoryService;

    public ProductController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    // Hiển thị danh sách product với phân trang, sắp xếp và lọc theo category
    @GetMapping
    public String listProducts(@RequestParam(value = "page", defaultValue = "0") int page,
                              @RequestParam(value = "sort", required = false, defaultValue = "") String sort,
                              @RequestParam(value = "categoryId", required = false) Long categoryId, Model model) {
        Sort.Direction direction = Sort.Direction.ASC;
        if ("price_desc".equals(sort)) {
            direction = Sort.Direction.DESC;
        }
        Pageable pageable = PageRequest.of(page, 5, Sort.by(direction, "price"));
        Page<Product> productPage;
        
        if (categoryId != null && categoryId > 0) {
            productPage = productService.getProductsByCategoryWithPagination(categoryId, pageable);
        } else {
            productPage = productService.getAllProductsWithPagination(pageable);
        }
        
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("hasNext", productPage.hasNext());
        model.addAttribute("hasPrevious", productPage.hasPrevious());
        model.addAttribute("totalElements", productPage.getTotalElements());
        model.addAttribute("currentSort", sort);
        model.addAttribute("selectedCategoryId", categoryId != null ? categoryId : 0);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "product/list";
    }

    // Hiển thị form thêm (chỉ ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "product/add";
    }

    // Lưu product (chỉ ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/save")
    public String saveProduct(@Valid @ModelAttribute("product") Product product, 
                             BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            return "product/add";
        }
        productService.saveProduct(product);
        return "redirect:/products";
    }

    // Hiển thị form edit (chỉ ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Integer id, Model model) {
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "product/edit";
    }

    // Xóa product (chỉ ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable("id") Integer id) {
        productService.deleteProduct(id);
        return "redirect:/products";
    }

    // Tìm kiếm product theo tên với phân trang, sắp xếp và lọc theo category
    @GetMapping("/search")
    public String searchProducts(@RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
                                @RequestParam(value = "page", defaultValue = "0") int page,
                                @RequestParam(value = "sort", required = false, defaultValue = "") String sort,
                                @RequestParam(value = "categoryId", required = false) Long categoryId, Model model) {
        Sort.Direction direction = Sort.Direction.ASC;
        if ("price_desc".equals(sort)) {
            direction = Sort.Direction.DESC;
        }
        Pageable pageable = PageRequest.of(page, 5, Sort.by(direction, "price"));
        Page<Product> productPage;
        
        if (keyword.isEmpty()) {
            if (categoryId != null && categoryId > 0) {
                productPage = productService.getProductsByCategoryWithPagination(categoryId, pageable);
            } else {
                productPage = productService.getAllProductsWithPagination(pageable);
            }
            model.addAttribute("searchMessage", "Danh sách tất cả sản phẩm");
        } else {
            if (categoryId != null && categoryId > 0) {
                productPage = productService.searchProductsByNameAndCategoryWithPagination(keyword, categoryId, pageable);
            } else {
                productPage = productService.searchProductsByNameWithPagination(keyword, pageable);
            }
            if (productPage.isEmpty()) {
                model.addAttribute("searchMessage", "Không tìm thấy sản phẩm với từ khóa: " + keyword);
            } else {
                model.addAttribute("searchMessage", "Tìm thấy " + productPage.getTotalElements() + " sản phẩm với từ khóa: " + keyword);
            }
        }
        
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("hasNext", productPage.hasNext());
        model.addAttribute("hasPrevious", productPage.hasPrevious());
        model.addAttribute("totalElements", productPage.getTotalElements());
        model.addAttribute("currentSort", sort);
        model.addAttribute("selectedCategoryId", categoryId != null ? categoryId : 0);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "product/list";
    }
}