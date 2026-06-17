package com.example.ecommerce.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.ecommerce.dto.ProductRequestDTO;
import com.example.ecommerce.dto.ProductResponseDTO;
import com.example.ecommerce.security.JwtAccessDeniedHandler;
import com.example.ecommerce.security.JwtAuthenticationEntryPoint;
import com.example.ecommerce.security.JwtService;
import com.example.ecommerce.service.CustomUserDetailedService;
import com.example.ecommerce.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ProductController.class)
@Import(com.example.ecommerce.controller.ProductControllerSecurityTest.TestSecurityConfig.class)
public class ProductControllerSecurityTest {

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
	
	//@Autowired
	//private org.springframework.web.context.WebApplicationContext context;
	
	//@Autowired
	//private org.springframework.security.web.FilterChainProxy springSecurityFilterChain;
	
	@TestConfiguration
	static class TestSecurityConfig {

	    @Bean
	    SecurityFilterChain securityFilterChain(HttpSecurity http)
	            throws Exception {

	        return http
	                .csrf(csrf -> csrf.disable())
	                .authorizeHttpRequests(auth -> auth
	                        .requestMatchers("/products/save")
	                        .hasRole("ADMIN")
	                        .anyRequest()
	                        .authenticated())
	                .build();
	    }
	}
	
	@Test
	@WithMockUser(roles = "ADMIN")
	void shouldAccessSimpleEndpoint1() throws Exception {

	    when(productService.findAllProductsWithDTOProjection())
	            .thenReturn(List.of());

	    mockMvc.perform(get("/products"))
	            .andDo(print())
	            .andExpect(status().isOk());

	    verify(productService).findAllProductsWithDTOProjection();
	}

	 @Test
	 @WithMockUser(roles = "ADMIN")
	 void shouldAccessSimpleEndpoint() throws Exception {
	 
	 mockMvc.perform(get("/products")).andDo(print()); 
	 }
	
	@Test
	void shouldRejectAnonymousUserWhenSavingProduct() throws Exception {

		ProductRequestDTO request = new ProductRequestDTO();
		request.setName("Laptop");
		request.setDescription("Gaming Laptop");
		request.setPrice(50000.0);
		request.setStockQuantity(10);
		request.setCategory("ELECTRONICS");

		mockMvc.perform(post("/products/save").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))).andDo(print()).andExpect(status().isForbidden());

		verify(productService, never()).saveProduct(any());
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

		when(productService.saveProduct(any())).thenReturn(response);

		mockMvc.perform(post("/products/save").with(csrf()) // IMPORTANT
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)))
				.andDo(print()).andExpect(status().isCreated());
	}

	@Test
	@WithMockUser(roles = "USER")
	void shouldRejectUserRoleWhenSavingProduct() throws Exception {

		ProductRequestDTO request = new ProductRequestDTO();
		request.setName("Laptop");
		request.setDescription("Gaming Laptop");
		request.setPrice(50000.0);
		request.setStockQuantity(10);
		request.setCategory("ELECTRONICS");

		mockMvc.perform(post("/products/save").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))).andExpect(status().isForbidden());
		verify(productService, never()).saveProduct(any());
	}

}
