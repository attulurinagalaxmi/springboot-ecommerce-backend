package com.example.ecommerce.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.ecommerce.dto.ProductRequestDTO;
import com.example.ecommerce.dto.ProductResponseDTO;
import com.example.ecommerce.dto.ProductSummaryDTO;
import com.example.ecommerce.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Product APIs", description = "Operations related to products")
@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
@SecurityRequirement(name = "bearerAuth")
public class ProductController {

	private final ProductService productService;

	@Operation(summary = "Get all products")
	@GetMapping
	public ResponseEntity<List<ProductSummaryDTO>> getAllProducts() {
		return ResponseEntity.ok(productService.findAllProductsWithDTOProjection());
	}

	@Operation(summary = "Get Product by Id")
	@GetMapping("/{id}")
	public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable Long id) {
		return ResponseEntity.ok(productService.getProductById(id));
	}

	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Save Product")
	@PostMapping("/save")
	public ResponseEntity<ProductResponseDTO> saveProduct(@Valid @RequestBody ProductRequestDTO productDto) {
		ProductResponseDTO dto = productService.saveProduct(productDto);
		return new ResponseEntity<>(dto, HttpStatus.CREATED);

	}

	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Update Product by Id")
	@PutMapping("/{id}")
	public ResponseEntity<ProductResponseDTO> updateProduct(@PathVariable Long id,
			@Valid @RequestBody ProductRequestDTO productDTO) {

		return ResponseEntity.ok(productService.updateProduct(id, productDTO));
	}

	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Delete Product by Id")
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
		return ResponseEntity.ok(productService.deleteProduct(id));
	}

	@Operation(summary = "Get Products with Pagination")
	@GetMapping("/paged")
	public ResponseEntity<Page<ProductResponseDTO>> getProductsWithPagination(

			@RequestParam(defaultValue = "0") int page,

			@RequestParam(defaultValue = "5") int size,

			@RequestParam(defaultValue = "name") String sortBy) {

		return ResponseEntity.ok(productService.getProductsWithPagination(page, size, sortBy));
	}

	@GetMapping("/search")
	public Page<ProductResponseDTO> searchProducts(

			@RequestParam(required = false) String category,

			@RequestParam(required = false) String name,

			@RequestParam(required = false) Double minPrice,

			@RequestParam(required = false) Double maxPrice,
			
			Pageable pageable) {
		

		return productService.searchProducts(category, name, minPrice, maxPrice, pageable);
	}
	
	@GetMapping("/searchByCb")
	public List<ProductResponseDTO> searchProductsByCb(

			@RequestParam(required = false) String category,

			@RequestParam(required = false) String name,

			@RequestParam(required = false) Double minPrice,

			@RequestParam(required = false) Double maxPrice) {
		

		return productService.searchProductsByCritiria(category, minPrice);
	}

}
