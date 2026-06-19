package com.example.ecommerce.dto;

import java.sql.Timestamp;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WishlistResponseDTO {
	
    private String userEmail;

    private Timestamp createdAt;

    private List<ProductResponseDTO> products;

}
