package com.example.ecommerce.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.example.ecommerce.dto.ProductRequestDTO;
import com.example.ecommerce.dto.ProductResponseDTO;
import com.example.ecommerce.model.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {
	
	@Mapping(target = "category", ignore = true)
	ProductResponseDTO toDTO(Product product);
	
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "category", ignore = true)
	Product toEntity(ProductRequestDTO dto);
	
	void updateProductFromDto(
			ProductRequestDTO dto,
            @MappingTarget Product product
    );
}
