package com.example.ecommerce.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.ecommerce.model.Product;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {

	private final EntityManager entityManager;

//Spring Data JPA creates the CriteriaBuilder, CriteriaQuery, and Root for you when using Specifications. In a custom repository, you are working directly with the Criteria API, so you must create them yourself.

	@Override
	public List<Product> searchProductsCustom(String category, Double minPrice) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();

		CriteriaQuery<Product> query = cb.createQuery(Product.class);

		Root<Product> root = query.from(Product.class);

		List<Predicate> predicates = new ArrayList<>();

		if (category != null) {
			predicates.add(cb.equal(root.get("category"), category));
		}

		if (minPrice != null) {
			predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
		}

		query.where(predicates.toArray(new Predicate[0]));

		return entityManager.createQuery(query).getResultList();
	}

}
