package com.example.ecommerce.repository;

import java.util.List;

import com.example.ecommerce.model.Product;

public interface ProductRepositoryCustom {

	 List<Product> searchProductsCustom(
	            String category,
	            Double minPrice);
}
