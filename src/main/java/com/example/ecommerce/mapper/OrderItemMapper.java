package com.example.ecommerce.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.ecommerce.dto.OrderItemRequestDTO;
import com.example.ecommerce.dto.OrderItemResponseDTO;
import com.example.ecommerce.model.OrderItem;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

	OrderItem toEntiry(OrderItemRequestDTO reqDTO);
	
	@Mapping(source = "product.id",target = "productId")
	@Mapping(source = "product.name",target = "productName")
	@Mapping(expression = "java(orderItem.getPrice() * orderItem.getQuantity())", target = "subtotal")
	OrderItemResponseDTO toDTO(OrderItem orderItem);
}
