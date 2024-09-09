package com.daewon.xeno_backend.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartDTO {

    private Long cartId;
    private Long productOptionId;
    private Long productId;
    private Long quantity;
    private String brandName;
    private String productName;
    private String color;
    private String size;
    private String productImage;
    private Long price;
    private boolean isSale;
}
