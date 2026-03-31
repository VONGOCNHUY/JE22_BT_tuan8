package com.example.demo.controller;

import com.example.demo.model.Account;
import com.example.demo.model.CartItem;
import com.example.demo.model.Order;
import com.example.demo.service.AccountService;
import com.example.demo.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private AccountService accountService;

    // Hiển thị form checkout
    @GetMapping("/checkout")
    public String showCheckout(HttpSession session, Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) {
            return "redirect:/cart";
        }

        long totalPrice = 0;
        for (CartItem item : cart) {
            totalPrice += item.getTotalPrice();
        }

        model.addAttribute("cartItems", cart);
        model.addAttribute("totalPrice", totalPrice);
        return "order/checkout";
    }

    // Xử lý thanh toán
    @PostMapping("/place-order")
    public String placeOrder(HttpSession session, Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) {
            return "redirect:/cart";
        }

        // Lấy thông tin tài khoản
        Account account = accountService.getAccountByUsername(authentication.getName());
        if (account == null) {
            model.addAttribute("error", "Không tìm thấy tài khoản");
            return "order/checkout";
        }

        // Tạo order
        Order order = orderService.createOrder(account, cart);

        // Xóa giỏ hàng sau khi đặt hàng thành công
        session.removeAttribute("cart");

        model.addAttribute("orderId", order.getId());
        model.addAttribute("totalPrice", order.getTotalPrice());
        return "order/success";
    }

    // Xem chi tiết order
    @GetMapping("/{id}")
    public String viewOrder(@PathVariable("id") Long orderId, Model model) {
        Order order = orderService.getOrderById(orderId);
        if (order == null) {
            return "redirect:/products";
        }
        model.addAttribute("order", order);
        return "order/detail";
    }
}
