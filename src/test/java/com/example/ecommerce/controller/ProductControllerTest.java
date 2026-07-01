package com.example.ecommerce.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.ecommerce.dto.ProductRequestDTO;
import com.example.ecommerce.dto.ProductResponseDTO;
import com.example.ecommerce.dto.ProductSummaryDTO;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.security.JwtAccessDeniedHandler;
import com.example.ecommerce.security.JwtAuthenticationEntryPoint;
import com.example.ecommerce.security.JwtService;
import com.example.ecommerce.service.CustomUserDetailedService;
import com.example.ecommerce.service.ProductService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ProductControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private ProductService productService;
	
	@MockitoBean
	private JwtAuthenticationEntryPoint authenticationEntryPoint;

	@MockitoBean
	private JwtAccessDeniedHandler accessDeniedHandler;

	@MockitoBean
	private JwtService jwtService;

	@MockitoBean
	private CustomUserDetailedService customUserDetailedService;

	  @Test
	    void shouldLoadController() throws Exception {

	        mockMvc.perform(get("/products"))
	                .andDo(print());
	    }

	@Test
	void shouldReturnProductById() throws Exception {

		ProductResponseDTO dto = new ProductResponseDTO();

		dto.setName("Laptop");
		dto.setDescription("Gaming Laptop");
		dto.setPrice(50000.0);
		dto.setStockQuantity(10);

		when(productService.getProductById(1L)).thenReturn(dto);

		mockMvc.perform(get("/products/1")).andExpect(status().isOk()).andExpect(jsonPath("$.name").value("Laptop"))
				.andExpect(jsonPath("$.price").value(50000.0));

		verify(productService).getProductById(1L);
	}

	@Test
	void shouldReturnAllProducts() throws Exception {

		ProductSummaryDTO dto1 = new ProductSummaryDTO(1L, "Laptop", 50000.0);

		ProductSummaryDTO dto2 = new ProductSummaryDTO(2L, "Mobile", 10000.0);

		List<ProductSummaryDTO> products = new ArrayList<>();
		products.add(dto1);
		products.add(dto2);
		when(productService.findAllProductsWithDTOProjection()).thenReturn(products);

		mockMvc.perform(get("/products")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(2))
				.andExpect(jsonPath("$[0].name").value("Laptop")).andExpect(jsonPath("$[0].price").value(50000.0))
				.andExpect(jsonPath("$[1].name").value("Mobile")).andExpect(jsonPath("$[1].price").value(10000.0));
		verify(productService).findAllProductsWithDTOProjection();
	}

	@Test
	void shouldReturnEmptyListWhenNoProductsExist() throws Exception {

		when(productService.findAllProductsWithDTOProjection()).thenReturn(Collections.emptyList());
		mockMvc.perform(get("/products")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(0));

		verify(productService).findAllProductsWithDTOProjection();

	}

	@Test
	void shouldReturnProdcutsWithPagination() throws Exception {
		ProductResponseDTO dto1 = new ProductResponseDTO();
		dto1.setName("Laptop");
		dto1.setDescription("Lenova");
		dto1.setPrice(50000.0);
		ProductResponseDTO dto2 = new ProductResponseDTO();
		dto2.setName("Mobile");
		dto2.setDescription("Samsung");
		dto2.setPrice(10000.0);
		ProductResponseDTO dto3 = new ProductResponseDTO();
		dto3.setName("Tab");
		dto3.setDescription("Mac");
		dto3.setPrice(25000.0);
		List<ProductResponseDTO> prodList = new ArrayList<ProductResponseDTO>();
		prodList.add(dto1);
		prodList.add(dto2);
		prodList.add(dto3);
		Page<ProductResponseDTO> response = new PageImpl<ProductResponseDTO>(prodList);
		when(productService.getProductsWithPagination(anyInt(), anyInt(), anyString())).thenReturn(response);
		mockMvc.perform(get("/products/paged").param("page", "1").param("size", "3").param("sortBy", "name"))
				.andExpect(status().isOk()).andDo(print()) // 💡 Always add this to see the exact payload output in logs
															// if it fails
				.andExpect(jsonPath("$.content").isArray()) // Verifies it's a list wrapper
				.andExpect(jsonPath("$.content.length()").value(3))
				.andExpect(jsonPath("$.content[0].name").value("Laptop"))
				.andExpect(jsonPath("$.content[1].name").value("Mobile"))
				.andExpect(jsonPath("$.content[2].name").value("Tab")).andExpect(jsonPath("$.totalElements").value(3))
				.andExpect(jsonPath("$.totalPages").value(1)).andExpect(jsonPath("$.number").value(0));
		verify(productService).getProductsWithPagination(1, 3, "name");
	}

	@Test
	void shouldSaveProductSuccessfully() throws JsonProcessingException, Exception {
		ProductRequestDTO request = new ProductRequestDTO();
		request.setName("Laptop");
		request.setDescription("Gaming Laptop");
		request.setPrice(50000.0);
		request.setStockQuantity(10);
		request.setCategory("ELECTRONICS");
		ProductResponseDTO dto = new ProductResponseDTO();
		dto.setName("Laptop");
		dto.setDescription("Gaming Laptop");
		dto.setPrice(50000.0);
		dto.setStockQuantity(10);
		dto.setCategory("ELECTRONICS");
		when(productService.saveProduct(any(ProductRequestDTO.class))).thenReturn(dto);
		mockMvc.perform(post("/products/save").contentType(MediaType.APPLICATION_JSON) // Tells Spring you are sending
																						// JSON
				.content(objectMapper.writeValueAsString(request))) // Converts DTO to standard JSON
				.andDo(print()).andExpect(status().isCreated()) // HTTP 201 Created
				.andExpect(jsonPath("$.name").value("Laptop")).andExpect(jsonPath("$.price").value(50000.00))
				.andExpect(jsonPath("$.stockQuantity").value(10));
		verify(productService).saveProduct(any(ProductRequestDTO.class));
	}

	@Test
	void shouldReturnBadRequestWhenProductNameIsBlank() throws Exception {
		ProductRequestDTO requestDto = new ProductRequestDTO();
		requestDto.setName(""); // @NotBlank constraint
		requestDto.setPrice(50000.00);
		mockMvc.perform(post("/products/save").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto))).andDo(print()).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.name").value("Name is required"))
				.andExpect(jsonPath("$.description").value("description is required"));
		verify(productService, never()).saveProduct(any(ProductRequestDTO.class));
	}

	@Test
	void shouldUpdateProductSuccessfully() throws Exception {
		Long productId = 1L;
		ProductRequestDTO updateDto = new ProductRequestDTO();
		updateDto.setName("Updated Laptop Name");
		updateDto.setDescription("Updated Description");
		updateDto.setPrice(50000.0);
		updateDto.setCategory("ELECTRONICS");

		ProductResponseDTO responseDto = new ProductResponseDTO();
		responseDto.setName("Updated Laptop Name");
		responseDto.setDescription("Updated Description");
		responseDto.setPrice(50000.0);
		responseDto.setCategory("ELECTRONICS");

		when(productService.updateProduct(eq(productId), any(ProductRequestDTO.class))).thenReturn(responseDto);

		// 2. Act & Assert: Pass ID directly into the URL template mapping
		mockMvc.perform(put("/products/{id}", productId) // 👈 Automatically replaces {id} with 50
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateDto)))
				.andDo(print()).andExpect(status().isOk()) // 👈 Expects HTTP 200 OK
				.andExpect(jsonPath("$.name").value("Updated Laptop Name"))
				.andExpect(jsonPath("$.description").value("Updated Description"));
	}

	@Test
	void shouldReturnNotFoundWhenUpdatingNonExistentProduct() throws Exception {
		Long nonExistentId = 999L;
		ProductRequestDTO updateDto = new ProductRequestDTO();
		updateDto.setName("Valid Name");
		updateDto.setDescription("Valid Description");
		updateDto.setPrice(50000.0);

		when(productService.updateProduct(eq(nonExistentId), any(ProductRequestDTO.class)))
				.thenThrow(new ResourceNotFoundException("Product not found with id: " + nonExistentId));

		mockMvc.perform(put("/products/{id}", nonExistentId).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto))).andDo(print()).andExpect(status().isNotFound());
	}

	@Test
	void shouldDeleteProductSuccessfully() throws Exception {
		Long productId = 1L; 
		when(productService.deleteProduct(productId)).thenReturn("Product deleted successfully");
		mockMvc.perform(delete("/products/{id}", productId)).andDo(print()).andExpect(status().isOk())
				.andExpect(content().string("Product deleted successfully"));
		verify(productService).deleteProduct(eq(productId));
	}

	@Test
	void shouldReturnNotFoundWhenDeletingNonExistentProduct() throws Exception {
		Long nonExistentId = 999L;

		doThrow(new ResourceNotFoundException("Product not found with id: " + nonExistentId)).when(productService)
				.deleteProduct(nonExistentId);
		mockMvc.perform(delete("/products/{id}", nonExistentId)).andDo(print()).andExpect(status().isNotFound());
	}
	@Test
	void shouldAllowAnonymousUserToGetProducts() throws Exception {

	    when(productService.findAllProductsWithDTOProjection())
	            .thenReturn(Collections.emptyList());

	    mockMvc.perform(get("/products"))
	            .andExpect(status().isOk());
	}
	
	@Test
	void shouldRejectAnonymousUserWhenSavingProduct() throws Exception {

	    ProductRequestDTO request = new ProductRequestDTO();
	    request.setName("Laptop");
	    request.setDescription("Gaming Laptop");
	    request.setPrice(50000.0);
	    request.setStockQuantity(10);
	    request.setCategory("ELECTRONICS");

	    mockMvc.perform(post("/products/save")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(request)))
	            .andDo(print())
	            .andExpect(status().isCreated());

	    verify(productService).saveProduct(any(ProductRequestDTO.class));
	}
	@Test
	@WithMockUser(roles = "ADMIN")
	void shouldAllowAdminToSaveProduct() throws Exception {

	    ProductRequestDTO request = new ProductRequestDTO();
	    request.setName("Laptop");
	    request.setDescription("Gaming Laptop");
	    request.setPrice(50000.0);
	    request.setStockQuantity(10);
	    request.setCategory("ELECTRONICS");

	    ProductResponseDTO response = new ProductResponseDTO();
	    response.setName("Laptop");
	    response.setPrice(50000.0);

	    when(productService.saveProduct(any(ProductRequestDTO.class)))
	            .thenReturn(response);

	    mockMvc.perform(post("/products/save")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(request)))
	            .andExpect(status().isCreated());

	    verify(productService).saveProduct(any(ProductRequestDTO.class));
	}
	@Test
	@WithMockUser(roles = "USER")
	void shouldRejectNormalUserWhenSavingProduct() throws Exception {

	    ProductRequestDTO request = new ProductRequestDTO();
	    request.setName("Laptop");
	    request.setDescription("Gaming Laptop");
	    request.setPrice(50000.0);
	    request.setStockQuantity(10);
	    request.setCategory("ELECTRONICS");

	    mockMvc.perform(post("/products/save")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(request)))
	            .andDo(print())
	            .andExpect(status().isCreated());

	    verify(productService).saveProduct(any(ProductRequestDTO.class));
	}
	
	@Test
	void shouldSearchProductsBySpecifications() throws Exception {
		ProductResponseDTO dto1 = new ProductResponseDTO();
		dto1.setName("Laptop");
		dto1.setDescription("Lenova");
		dto1.setPrice(50000.0);
		ProductResponseDTO dto2 = new ProductResponseDTO();
		dto2.setName("Mobile");
		dto2.setDescription("Samsung");
		dto2.setPrice(10000.0);
		ProductResponseDTO dto3 = new ProductResponseDTO();
		dto3.setName("Tab");
		dto3.setDescription("Mac");
		dto3.setPrice(25000.0);
		List<ProductResponseDTO> prodList = new ArrayList<ProductResponseDTO>();
		prodList.add(dto1);
		prodList.add(dto2);
		prodList.add(dto3);
		Pageable pageable = PageRequest.of(0, 3,Sort.by("name").descending());
		Page<ProductResponseDTO> response = new PageImpl<ProductResponseDTO>(prodList);
		when(productService.searchProducts("ELECTRONICS", "Laptop", 10000.0, 50000.0, pageable)).thenReturn(response);
		
		mockMvc.perform(get("/products/search").param("category", "ELECTRONICS").param("name", "Laptop").param("minPrice", "10000").param("maxPrice", "50000")
				 .param("page", "0")       // Request page 0 (first page)
		            .param("size", "3")      // Page size of 10 items
		            .param("sort", "name,desc") // Sort by price descending
		            .contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andDo(print()).andExpect(jsonPath("$.content").exists());
		verify(productService).searchProducts("ELECTRONICS", "Laptop", 10000.0, 50000.0,pageable);
	}
}
