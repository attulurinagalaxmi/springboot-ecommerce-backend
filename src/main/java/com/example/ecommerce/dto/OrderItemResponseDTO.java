package com.example.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponseDTO {

	 private Long productId;

	    private String productName;

	    private Integer quantity;

	    private Double price;

	    private Double subtotal;
}
