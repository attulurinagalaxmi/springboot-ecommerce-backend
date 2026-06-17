package com.example.ecommerce.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.example.ecommerce.model.Category;
import com.example.ecommerce.model.Product;

@DataJpaTest
@Testcontainers
@ActiveProfiles("test")
public class ProductRepositoryPostgresTest {

	@Container
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17").withDatabaseName("testdb")
			.withUsername("postgres").withPassword("postgres");
	@Autowired
	private ProductRepository productRepository;

	@DynamicPropertySource
	static void configureProperties(DynamicPropertyRegistry registry) {

		registry.add("spring.datasource.url", postgres::getJdbcUrl);

		registry.add("spring.datasource.username", postgres::getUsername);

		registry.add("spring.datasource.password", postgres::getPassword);
	}

	@Test
	void shouldSaveProductInPostgres() {
		Product product = new Product();

		product.setName("Laptop");
		product.setDescription("Gaming Laptop");
		product.setPrice(50000.0);
		product.setStockQuantity(10);
		product.setCategory(Category.ELECTRONICS);

		Product saved = productRepository.save(product);

		assertNotNull(saved.getId());

		assertEquals("Laptop", saved.getName());
	}
}
