package com.example.ecommerce.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDTO {

	private Long orderId;

    private String userEmail;

    private Double totalAmount;

    private List<OrderItemResponseDTO> items;
}
