package com.example.ecommerce.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.example.ecommerce.dto.ProductSummaryDTO;
import com.example.ecommerce.model.Category;
import com.example.ecommerce.model.Product;

@DataJpaTest
@ActiveProfiles("test")
public class ProductRepositoryTest {

	@Autowired
	private ProductRepository productRepository;

	@Test
	void shouldSaveProduct() {
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
	@Test
	void shouldFindSavedProduct() {
		Optional<Product> product = productRepository.findById(1L);
		assertNotNull(product);
	}
	@Test
	void shouldReturnProjectedProducts() {
		
		Product product = new Product();

		product.setName("Laptop");
		product.setDescription("Gaming Laptop");
		product.setPrice(50000.0);
		product.setStockQuantity(10);
		product.setCategory(Category.ELECTRONICS);

		productRepository.save(product);

		Product product2 = new Product();

		product2.setName("Mobile");
		product2.setDescription("Samsung");
		product2.setPrice(10000.0);
		product2.setStockQuantity(20);
		product2.setCategory(Category.ELECTRONICS);

		productRepository.save(product2);
		
		List<ProductSummaryDTO> result = productRepository.findAllProjectedBy();
		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals(1L, result.get(0).id());
		assertEquals("Mobile", result.get(1).name());
		
		
	}
}
