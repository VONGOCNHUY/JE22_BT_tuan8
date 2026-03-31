package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {

    private Long productId;
    private String productName;
    private long price;
    private String image;
    private int quantity;

    public long getTotalPrice() {
        return price * quantity;
    }
}
