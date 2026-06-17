package com.example.ecommerce.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestDTO {
	
	@Schema(example = "Laptop")
	@NotBlank(message = "Name is required")
	private String name;
	
	@Schema(example = "Product description")
	@NotBlank(message = "description is required")
	private String description;
	
	@Schema(example = "10000")
	@NotNull(message = "Price is required")
	@Positive
	private Double price;
	
	@Min(0)
	private int stockQuantity;
	
	private String category;

}
