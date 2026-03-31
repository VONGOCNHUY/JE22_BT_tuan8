package com.example.demo.controller;

import com.example.demo.model.CartItem;
import com.example.demo.model.Product;
import com.example.demo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private ProductService productService;

    // Lấy giỏ hàng từ session
    private List<CartItem> getCart(HttpSession session) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute("cart", cart);
        }
        return cart;
    }

    // Thêm vào giỏ hàng
    @GetMapping("/add/{id}")
    public String addToCart(@PathVariable("id") Long productId, 
                           @RequestParam(value = "quantity", defaultValue = "1") int quantity,
                           HttpSession session) {
        Product product = productService.getProductById(productId.intValue());
        if (product != null) {
            List<CartItem> cart = getCart(session);
            
            // Kiểm tra xem product đã có trong giỏ chưa
            CartItem existingItem = cart.stream()
                    .filter(item -> item.getProductId().equals(productId))
                    .findFirst()
                    .orElse(null);
            
            if (existingItem != null) {
                existingItem.setQuantity(existingItem.getQuantity() + quantity);
            } else {
                CartItem newItem = new CartItem();
                newItem.setProductId(productId);
                newItem.setProductName(product.getName());
                newItem.setPrice(product.getPrice());
                newItem.setImage(product.getImage());
                newItem.setQuantity(quantity);
                cart.add(newItem);
            }
            
            session.setAttribute("cart", cart);
        }
        return "redirect:/products";
    }

    // Xem giỏ hàng
    @GetMapping
    public String viewCart(HttpSession session, Model model) {
        List<CartItem> cart = getCart(session);
        long totalPrice = 0;
        for (CartItem item : cart) {
            totalPrice += item.getTotalPrice();
        }
        model.addAttribute("cartItems", cart);
        model.addAttribute("totalPrice", totalPrice);
        return "cart/view";
    }

    // Cập nhật số lượng
    @PostMapping("/update/{id}")
    public String updateCart(@PathVariable("id") Long productId,
                            @RequestParam("quantity") int quantity,
                            HttpSession session) {
        List<CartItem> cart = getCart(session);
        CartItem item = cart.stream()
                .filter(i -> i.getProductId().equals(productId))
                .findFirst()
                .orElse(null);
        
        if (item != null) {
            if (quantity > 0) {
                item.setQuantity(quantity);
            } else {
                cart.remove(item);
            }
        }
        
        session.setAttribute("cart", cart);
        return "redirect:/cart";
    }

    // Xóa khỏi giỏ hàng
    @GetMapping("/remove/{id}")
    public String removeFromCart(@PathVariable("id") Long productId, HttpSession session) {
        List<CartItem> cart = getCart(session);
        cart.removeIf(item -> item.getProductId().equals(productId));
        session.setAttribute("cart", cart);
        return "redirect:/cart";
    }

    // Xóa toàn bộ giỏ hàng
    @GetMapping("/clear")
    public String clearCart(HttpSession session) {
        session.removeAttribute("cart");
        return "redirect:/cart";
    }
}
