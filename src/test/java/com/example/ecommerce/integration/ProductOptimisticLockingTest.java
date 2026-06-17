package com.example.ecommerce.integration;

import static org.junit.Assert.assertThrows;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.support.TransactionTemplate;

import com.example.ecommerce.model.Product;
import com.example.ecommerce.repository.ProductRepository;

import jakarta.transaction.Transactional;

/*Implemented optimistic locking using JPA @Version
to prevent lost updates during concurrent modifications.
*/
@SpringBootTest
@Transactional
public class ProductOptimisticLockingTest {
	
	@Autowired
    ProductRepository productRepository;
	
	 @Autowired
	    TransactionTemplate transactionTemplate;
	 
	 @Test
	 void testOptimesticLockingOnProduct() {
		 Long id = 1L;
		 Optional<Product> p1 = productRepository.findById(id);
		 System.out.println(p1.get().getVersion());
		 Product p2 = productRepository.findById(id).get();

		 p1.get().setPrice(100.0);
		 productRepository.saveAndFlush(p1.get());

		 p2.setPrice(200.0);

		 assertThrows(
		         ObjectOptimisticLockingFailureException.class,
		         () -> productRepository.saveAndFlush(p2)
		 );
	 }

	    @Test
	    void shouldThrowOptimisticLockExceptionReal() {

	        Product saved = transactionTemplate.execute(status -> {

	            Product p = new Product();

	            p.setName("Laptop");
	            p.setDescription("Gaming Laptop");
	            p.setPrice(50000.0);
	            p.setStockQuantity(10);

	            return productRepository.save(p);
	        });

	        Long id = saved.getId();

	        Product userA = transactionTemplate.execute(status ->
	                productRepository.findById(id).orElseThrow()
	        );

	        Product userB = transactionTemplate.execute(status ->
	                productRepository.findById(id).orElseThrow()
	        );

	        transactionTemplate.executeWithoutResult(status -> {

	            userA.setPrice(60000.0);

	            productRepository.saveAndFlush(userA);
	        });

	        assertThrows(
	                ObjectOptimisticLockingFailureException.class,
	                () -> transactionTemplate.executeWithoutResult(status -> {

	                    userB.setPrice(55000.0);

	                    productRepository.saveAndFlush(userB);
	                })
	        );
	    }


    @Test
    void shouldThrowOptimisticLockException() {

        Product product = new Product();

        product.setName("Ipad");
        product.setDescription("Gaming Ipad");
        product.setPrice(70000.0);
        product.setStockQuantity(10);

        Product saved = productRepository.saveAndFlush(product);

        Long id = saved.getId();

        Product product1 =
                productRepository.findById(id).orElseThrow();

        Product product2 =
                productRepository.findById(id).orElseThrow();

        product1.setPrice(69000.0);

        productRepository.saveAndFlush(product1);

        product2.setPrice(75000.0);

        assertThrows(
                ObjectOptimisticLockingFailureException.class,
                () -> productRepository.saveAndFlush(product2)
        );
    }
}