package com.example.ecommerce.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.ecommerce.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

	//Optional<Order> findByIdAndUserEmail(Long id, String email);

	@Query("""
			    SELECT DISTINCT o
			    FROM Order o
			    JOIN FETCH o.orderItems oi
			    JOIN FETCH oi.product
			    WHERE o.user.email = :email
			""")
	List<Order> findOrdersWithItemsAndProducts(String email);

	@Query("""
			    SELECT DISTINCT o
			    FROM Order o
			    JOIN FETCH o.orderItems
			    WHERE o.user.email = :email and o.id = :id
			""")
	Optional<Order> findOrdersWithItemsByIdAndUserEmail(Long id, String email);

	@EntityGraph(attributePaths = { "orderItems", "orderItems.product" })
	Optional<Order> findByIdAndUserEmail(Long id, String email);
}
