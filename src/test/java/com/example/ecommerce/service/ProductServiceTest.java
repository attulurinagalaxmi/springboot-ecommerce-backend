package com.example.ecommerce.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.example.ecommerce.dto.ProductRequestDTO;
import com.example.ecommerce.dto.ProductResponseDTO;
import com.example.ecommerce.dto.ProductSummaryDTO;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.mapper.ProductMapper;
import com.example.ecommerce.model.Category;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

	@Mock
	private ProductRepository productRepository;

	@Mock
	private ProductMapper productMapper;

	@InjectMocks
	private ProductService productService;

	@Test
	void shouldReturnProductWhenIdExists() {

		Product product = new Product();
		product.setId(1L);
		product.setName("Laptop");

		ProductResponseDTO dto = new ProductResponseDTO();
		dto.setPrice(110.0);
		;
		dto.setName("Laptop");

		when(productRepository.findById(1L)).thenReturn(Optional.of(product));

		when(productMapper.toDTO(product)).thenReturn(dto);

		ProductResponseDTO result = productService.getProductById(1L);

		assertEquals(110.0, result.getPrice());
		assertEquals("Laptop", result.getName());
		verify(productRepository).findById(1L);
		verify(productMapper).toDTO(product);
	}

	@Test
	void shouldThrowExceptionWhenProductNotFound() {
		when(productRepository.findById(1L)).thenReturn(Optional.empty());

		ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
				() -> productService.getProductById(1L));

		assertEquals("Product not found", exception.getMessage());
	}

	@Test
	void shouldSaveProductSuccessfully() {
		ProductRequestDTO request = new ProductRequestDTO();

		request.setName("Laptop");
		request.setDescription("Gaming Laptop");
		request.setPrice(50000.0);
		request.setStockQuantity(10);
		request.setCategory("ELECTRONICS");

		Product product = new Product();

		product.setName("Laptop");
		product.setDescription("Gaming Laptop");
		product.setPrice(50000.0);
		product.setStockQuantity(10);

		Product savedProduct = new Product();

		savedProduct.setId(1L);
		savedProduct.setName("Laptop");
		savedProduct.setDescription("Gaming Laptop");
		savedProduct.setPrice(50000.0);
		savedProduct.setStockQuantity(10);
		savedProduct.setCategory(Category.ELECTRONICS);

		ProductResponseDTO responseDTO = new ProductResponseDTO();

		responseDTO.setPrice(50000.0);
		responseDTO.setName("Laptop");
		when(productMapper.toEntity(request)).thenReturn(product);
		when(productRepository.save(any(Product.class))).thenReturn(savedProduct);
		when(productMapper.toDTO(savedProduct)).thenReturn(responseDTO);
		ProductResponseDTO result = productService.saveProduct(request);

		ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);

		verify(productRepository).save(captor.capture());

		Product capturedProduct = captor.getValue();
		assertEquals(Category.ELECTRONICS, capturedProduct.getCategory());

		assertNotNull(capturedProduct.getCreatedAt());

		assertNotNull(capturedProduct.getUpdatedAt());
		assertEquals(50000.0, result.getPrice());

	}

	@Test
	void shouldDeleteProductSuccessfully() {
		Long id = 1L;
		String successMsg = "product deleted successfully";
		Product product = new Product();
		product.setId(1L);
		product.setName("Laptop");
		product.setDescription("Gaming Laptop");
		product.setPrice(50000.0);
		product.setStockQuantity(10);
		product.setCategory(Category.ELECTRONICS);

		when(productRepository.findById(1L)).thenReturn(Optional.of(product));

		String result = productService.deleteProduct(id);
		assertEquals(successMsg, result);
		verify(productRepository).delete(product);

	}

	@Test
	void shouldThrowExceptionWhenProductNotFoundOnDetele() {
		when(productRepository.findById(1L)).thenReturn(Optional.empty());
		ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
				() -> productService.deleteProduct(1L));
		assertEquals("product not found", exception.getMessage());
		verify(productRepository, never()).delete(any(Product.class));
	}

	@Test
	void shouldUpdatePoductSuccessfully() {
		Product product = new Product();
		product.setId(1L);
		product.setName("Laptop");
		product.setDescription("Gaming Laptop");
		product.setPrice(50000.0);
		product.setStockQuantity(10);
		product.setCategory(Category.ELECTRONICS);

		when(productRepository.findById(1L)).thenReturn(Optional.of(product));

		ProductRequestDTO productDTO = new ProductRequestDTO();
		productDTO.setName("Lenova Laptop");
		productDTO.setDescription("IT Laptop");

		ProductResponseDTO dto = new ProductResponseDTO();
		dto.setPrice(50000.0);
		dto.setName("Lenova Laptop");
		dto.setDescription("IT Laptop");
		dto.setStockQuantity(10);

		when(productRepository.save(any(Product.class))).thenReturn(product);
		when(productMapper.toDTO(product)).thenReturn(dto);
		ProductResponseDTO result = productService.updateProduct(1L, productDTO);
		assertEquals(productDTO.getName(), result.getName());
		verify(productRepository).findById(1L);
		verify(productMapper).updateProductFromDto(productDTO, product);
		verify(productRepository).save(any(Product.class));
		verify(productMapper).toDTO(product);
	}

	@Test
	void shouldThrowExceptionWhenProductNotFoundOnUpdate() {
		ProductRequestDTO productDTO = new ProductRequestDTO();
		productDTO.setName("Lenova Laptop");
		productDTO.setDescription("IT Laptop");
		when(productRepository.findById(1L)).thenReturn(Optional.empty());
		ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
				() -> productService.updateProduct(1L, productDTO));
		assertEquals("product not found", exception.getMessage());
		verify(productRepository, never()).save(any(Product.class));
		verify(productMapper, never()).updateProductFromDto(any(), any());
	}
	
	@Test
	void shouldReturnPaginatedProducts() {
		
		List<Product> productList = new ArrayList<>(3);
		Product p1 = new Product();
		p1.setId(1L);
		p1.setName("Laptop");
		p1.setDescription("Lenova");
		p1.setPrice(50000.0);
		p1.setStockQuantity(10);
		Product p2 = new Product();
		p2.setId(2L);
		p2.setName("Mobile");
		p2.setDescription("Samsung");
		p2.setPrice(10000.0);
		p2.setStockQuantity(20);
		Product p3 = new Product();
		p3.setId(3L);
		p3.setName("LionKing");
		p3.setDescription("Book");
		p3.setPrice(1000.0);
		p3.setStockQuantity(30);
		productList.add(p1);
		productList.add(p2);
		productList.add(p3);
		Page<Product> products = new PageImpl<Product>(productList);
		ProductResponseDTO responseDTO = new ProductResponseDTO();
		responseDTO.setName("Laptop");
		responseDTO.setDescription("Lenova");
		responseDTO.setPrice(50000.0);
		responseDTO.setStockQuantity(10);
		//ProductResponseDTO dto2 = new ProductResponseDTO();
		//ProductResponseDTO dto3 = new ProductResponseDTO();

		when(productRepository.findAll(any(Pageable.class))).thenReturn(products);
		//when(productMapper.toDTO(p1)).thenReturn(responseDTO);
		//when(productMapper.toDTO(p2)).thenReturn(dto2);
		//when(productMapper.toDTO(p3)).thenReturn(dto3);
		
		when(productMapper.toDTO(any(Product.class))).thenAnswer(invocation -> {
			Product p = invocation.getArgument(0);
			 ProductResponseDTO dto =
	                    new ProductResponseDTO();

	            dto.setName(p.getName());
	            dto.setDescription(p.getDescription());
	            dto.setPrice(p.getPrice());

	            return dto;
		});
		
		
		Page<ProductResponseDTO> result = productService.getProductsWithPagination(1, 3, "Name");
		assertEquals(3, result.getTotalElements());
		assertEquals(3, result.getContent().size());
		assertEquals("Laptop",result.getContent().get(0).getName());
		assertEquals("Mobile",result.getContent().get(1).getName());
		assertEquals("LionKing",result.getContent().get(2).getName());
	}
	@Test
	void shouldReturnAllProductsSuccessfully() {
		List<Product> productList = new ArrayList<>(3);
		Product p1 = new Product();
		p1.setId(1L);
		p1.setName("Laptop");
		p1.setDescription("Lenova");
		p1.setPrice(50000.0);
		p1.setStockQuantity(10);
		Product p2 = new Product();
		p2.setId(2L);
		p2.setName("Mobile");
		p2.setDescription("Samsung");
		p2.setPrice(10000.0);
		p2.setStockQuantity(20);
		Product p3 = new Product();
		p3.setId(3L);
		p3.setName("LionKing");
		p3.setDescription("Book");
		p3.setPrice(1000.0);
		p3.setStockQuantity(30);
		productList.add(p1);
		productList.add(p2);
		productList.add(p3);
		when(productRepository.findAll()).thenReturn(productList);
		when(productMapper.toDTO(any(Product.class))).thenAnswer(invocation -> {
			Product p = invocation.getArgument(0);
			 ProductResponseDTO dto =
	                    new ProductResponseDTO();

	            dto.setName(p.getName());
	            dto.setDescription(p.getDescription());
	            dto.setPrice(p.getPrice());

	            return dto;
		});
		List<ProductResponseDTO> result = productService.getAllProducts();
		assertEquals(3, result.size());
		assertEquals("Laptop", result.get(0).getName());
		verify(productRepository).findAll();
		verify(productMapper).toDTO(p1);
		verify(productMapper).toDTO(p2);
		verify(productMapper).toDTO(p3);
	}
	@Test
	void shouldReturnEmptyListForALLProducts(){
		List<Product> productList = new ArrayList<>();
		when(productRepository.findAll()).thenReturn(productList);
		List<ProductResponseDTO> result = productService.getAllProducts();
		assertEquals(0, result.size());
		verify(productRepository).findAll();
		verify(productMapper,never()).toDTO(any(Product.class));
	}
	
	@Test
	void shouldReturnAllProductsWithDTOProjection() {
		ProductSummaryDTO dto1 = new ProductSummaryDTO(1L,"Laptop", 50000.0);
		ProductSummaryDTO dto2 = new ProductSummaryDTO(2L,"Mobile", 10000.0);
		ProductSummaryDTO dto3 = new ProductSummaryDTO(3L,"LionKing", 1000.0);
		List<ProductSummaryDTO> response = new ArrayList<>();
		response.add(dto1);
		response.add(dto2);
		response.add(dto3);
		when(productRepository.findAllProjectedBy()).thenReturn(response);
		List<ProductSummaryDTO> result = productService.findAllProductsWithDTOProjection();
		assertEquals(response.size(), result.size());
		verify(productRepository).findAllProjectedBy();
	}

}
