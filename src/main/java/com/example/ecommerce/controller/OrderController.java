package com.example.ecommerce.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ecommerce.dto.OrderRequestDTO;
import com.example.ecommerce.dto.OrderResponseDTO;
import com.example.ecommerce.service.OrderService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@Tag(name = "Order APIs",
description = "Operations related to orders")
@RestController
@AllArgsConstructor
@RequestMapping("/orders")
@SecurityRequirement(name = "bearerAuth")
public class OrderController {
	
	private final OrderService orderService;
	
	
	@PostMapping("/placeOrder")
	public ResponseEntity<OrderResponseDTO> placeOrder(@Valid @RequestBody OrderRequestDTO orderRequest) {
		
		OrderResponseDTO response = orderService.placeOrder(orderRequest);
		return ResponseEntity.ok(response);
		
	}
	@GetMapping("/my-orders")
	public ResponseEntity<List<OrderResponseDTO>> userOrders(){
		return ResponseEntity.ok(orderService.getUserOrders());
		
	}

	@GetMapping("/{id}")
	public ResponseEntity<OrderResponseDTO> getOrdersById(@Valid @PathVariable Long id){
		return ResponseEntity.ok(orderService.getOrderById(id));
		
	}
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/all")
	public ResponseEntity<List<OrderResponseDTO>> GetALlOrders(){
		return ResponseEntity.ok(orderService.getAllOrders());
		
	}


}
