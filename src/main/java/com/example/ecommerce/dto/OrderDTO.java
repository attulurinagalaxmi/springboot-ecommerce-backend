package com.example.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
	
	 private Long id;
	
	@NotBlank(message = "Product name is required")
	private String productName;
	
	@NotBlank(message = "Price is required")
	private Double price;
	
	private Long userId;

}
