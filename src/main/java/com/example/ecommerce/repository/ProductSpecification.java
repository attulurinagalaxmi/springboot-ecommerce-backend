package com.example.ecommerce.repository;

import org.springframework.data.jpa.domain.Specification;

import com.example.ecommerce.model.Category;
import com.example.ecommerce.model.Product;

public class ProductSpecification {

	public static Specification<Product> hasCategory(String category) {

		return (root, query, cb) -> cb.equal(root.get("category"), Category.valueOf(category));
	}

	public static Specification<Product> hasName(String name) {

		return (root, query, cb) -> cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
	}

	public static Specification<Product> hasMinPrice(Double minPrice) {

		return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("price"), minPrice);
	}

	public static Specification<Product> hasMaxPrice(Double maxPrice) {

		return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("price"), maxPrice);
	}
}
