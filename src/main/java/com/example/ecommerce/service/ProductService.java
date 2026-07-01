package com.example.ecommerce.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.ecommerce.dto.ProductRequestDTO;
import com.example.ecommerce.dto.ProductResponseDTO;
import com.example.ecommerce.dto.ProductSummaryDTO;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.mapper.ProductMapper;
import com.example.ecommerce.model.Audit;
import com.example.ecommerce.model.Category;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.ProductSpecification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

	private final ProductRepository productRepository;

	private final ProductMapper productMapper;

	public List<ProductResponseDTO> getAllProducts() {
		return productRepository.findAll().stream().map(productMapper::toDTO).collect(Collectors.toList());
	}

	@Cacheable("products")
	public List<ProductSummaryDTO> findAllProductsWithDTOProjection() {
		System.out.println("Fetching from DB...");
		return productRepository.findAllProjectedBy();
	}

	public ProductResponseDTO getProductById(Long id) {

		Product product = productRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Product not found"));

		ProductResponseDTO dto = productMapper.toDTO(product);

		return dto;
	}

	@CacheEvict(value = "products", allEntries = true)
	@Audit(action = "CREATE_PRODUCT")
	public ProductResponseDTO saveProduct(ProductRequestDTO reqDTO) {

		log.info("Creating product: {}", reqDTO.getName());
		Product product = productMapper.toEntity(reqDTO);
		product.setCategory(ProductService.getMatchedEnum(reqDTO.getCategory()));
		product.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
		product.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
		Product savedPoduct = productRepository.save(product);

		log.info("Product created successfully with id={}", savedPoduct.getId());
		ProductResponseDTO response = productMapper.toDTO(savedPoduct);
		return response;
	}

	public static Category getMatchedEnum(String input) {
		if (input == null)
			return null;

		for (Category status : Category.values()) {
			if (status.name().equalsIgnoreCase(input)) {
				return status; // Returns the actual enum object
			}
		}
		return null;
	}

	@CacheEvict(value = "products", allEntries = true)
	@Audit(action = "UPDATE_PRODUCT")
	public ProductResponseDTO updateProduct(Long id, ProductRequestDTO productDTO) {

		Product product = productRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("product not found"));

		productMapper.updateProductFromDto(productDTO, product);

		Product updatedProduct = productRepository.save(product);

		ProductResponseDTO dto = productMapper.toDTO(updatedProduct);

		return dto;
	}

	@CacheEvict(value = "products", allEntries = true)
	@Audit(action = "DELETE_PRODUCT")
	public String deleteProduct(Long id) {

		Product product = productRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("product not found"));

		productRepository.delete(product);

		return "product deleted successfully";
	}

	public Page<ProductResponseDTO> getProductsWithPagination(int page, int size, String sortBy) {

		Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());

		Page<Product> productPage = productRepository.findAll(pageable);

		return productPage.map(productMapper::toDTO);
	}

	public Page<ProductResponseDTO> searchProducts(String category, String name, Double minPrice, Double maxPrice,
			Pageable pageable) {
		Specification<Product> spec = Specification.unrestricted();

		if (category != null) {
			spec = spec.and(ProductSpecification.hasCategory(category));
		}
		if (name != null) {

			spec = spec.and(ProductSpecification.hasName(name));
		}
		if (minPrice != null) {

			spec = spec.and(ProductSpecification.hasMinPrice(minPrice));
		}
		if (maxPrice != null) {

			spec = spec.and(ProductSpecification.hasMaxPrice(maxPrice));
		}
		Page<Product> products = productRepository.findAll(spec, pageable);
		return products.map(productMapper::toDTO);
	}

	public List<ProductResponseDTO> searchProductsByCritiria(String category, Double minPrice) {
		List<Product> products = productRepository.searchProductsCustom(category, minPrice);
		return products.stream().map(productMapper::toDTO).collect(Collectors.toList());
	}
}
