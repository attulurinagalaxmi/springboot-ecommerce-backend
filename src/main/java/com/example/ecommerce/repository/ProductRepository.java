package com.example.ecommerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.example.ecommerce.dto.ProductSummaryDTO;
import com.example.ecommerce.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long>,ProductRepositoryCustom,  JpaSpecificationExecutor<Product>{
	
	@Query("""
		    SELECT new com.example.ecommerce.dto.ProductSummaryDTO(
		        p.id,
		        p.name,
		        p.price
		    )
		    FROM Product p
		""")
	List<ProductSummaryDTO> findAllProjectedBy();

}
