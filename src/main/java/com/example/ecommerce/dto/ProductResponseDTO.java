package com.example.ecommerce.dto;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponseDTO {

	private String name;
	
	private String description;
	
	private Double price;
	
	private int stockQuantity;
	
	private String category;
	
	private Timestamp createdAt;
	
	private Timestamp updatedAt;

}
