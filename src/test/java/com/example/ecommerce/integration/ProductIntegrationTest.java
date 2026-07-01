package com.example.ecommerce.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.support.TransactionTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.example.ecommerce.model.AuditLog;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.repository.AuditLogRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.ProductSpecification;
import com.example.ecommerce.repository.WishlistRepository;
import com.example.ecommerce.service.ProductService;

import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletException;
import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Testcontainers
@ActiveProfiles("test")
@Transactional
//@Import(com.example.ecommerce.integration.ProductIntegrationTest.TestSecurityConfiguration.class)
public class ProductIntegrationTest {
	@Container
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17").withDatabaseName("testdb")
			.withUsername("postgres").withPassword("postgres");

	@DynamicPropertySource
	static void configureProperties(DynamicPropertyRegistry registry) {

		registry.add("spring.datasource.url", postgres::getJdbcUrl);

		registry.add("spring.datasource.username", postgres::getUsername);

		registry.add("spring.datasource.password", postgres::getPassword);
	}

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private WishlistRepository wishlistRepository;
	
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	EntityManager entityManager;
	
	@Autowired
    TransactionTemplate transactionTemplate;
	
	@Autowired
	private AuditLogRepository auditLogRepository;


	/*
	 * @TestConfiguration static class TestSecurityConfiguration {
	 * 
	 * @Bean SecurityFilterChain securityFilterChain(HttpSecurity http) throws
	 * Exception {
	 * 
	 * return http.csrf(csrf -> csrf.disable()).authorizeHttpRequests(auth ->
	 * auth.anyRequest().permitAll()) .build(); } }
	 */
	
	/*
	 * @Test
	 * 
	 * @WithMockUser(roles = "ADMIN") void testOptimesticLockingOnProduct() throws
	 * Exception { String requestJson = """ { "name":"Laptop",
	 * "description":"Gaming Laptop", "price":50000, "stockQuantity":10,
	 * "category":"ELECTRONICS" } """;
	 * 
	 * mockMvc.perform(post("/products/save").contentType(MediaType.APPLICATION_JSON
	 * ).content(requestJson)) .andExpect(status().isCreated());
	 * 
	 * Long id = 1L; Product p1 = productRepository.findById(id).get();
	 * System.out.println("p1 version:"+p1.getVersion());
	 * 
	 * entityManager.detach(p1);
	 * 
	 * Product p2 = productRepository.findById(id).get();
	 * System.out.println("p2 version : "+p2.getVersion());
	 * 
	 * transactionTemplate.executeWithoutResult(status -> {
	 * 
	 * p1.setPrice(60000.0);
	 * 
	 * productRepository.saveAndFlush(p1);
	 * System.out.println("p1 version after save :"+p1.getVersion());
	 * 
	 * });
	 * 
	 * 
	 * assertThrows( ObjectOptimisticLockingFailureException.class, () ->
	 * transactionTemplate.executeWithoutResult(status -> {
	 * 
	 * p2.setPrice(55000.0);
	 * System.out.println("p2 version before save: "+p2.getVersion());
	 * 
	 * productRepository.saveAndFlush(p2);
	 * 
	 * }) ); }
	 */
	@Test
	@WithMockUser(roles = "ADMIN")
	void shouldSaveProductToDatabase() throws Exception {

		String requestJson = """
				{
				  "name":"Laptop",
				  "description":"Gaming Laptop",
				  "price":50000,
				  "stockQuantity":10,
				  "category":"ELECTRONICS"
				}
				""";

		mockMvc.perform(post("/products/save").contentType(MediaType.APPLICATION_JSON).content(requestJson))
				.andExpect(status().isCreated());

		List<Product> products = productRepository.findAll();
		
		assertEquals(1, products.size());
		assertEquals("Laptop", products.get(0).getName());
		
		List<AuditLog> auditLogs = auditLogRepository.findAll();
		assertEquals(1, auditLogs.size());
		assertEquals("CREATE_PRODUCT", auditLogs.get(0).getAction());
		System.out.println("username in audit logs"+auditLogs.get(0).getUsername());
		assertNotNull(auditLogs.get(0).getUsername());
		
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void shouldCheckValidationOfSaveProduct() throws Exception {

		String requestJson = """
				{
				  "name":"",
				  "description":"Gaming Laptop",
				  "price":50000,
				  "stockQuantity":10,
				  "category":"ELECTRONICS"
				}
				""";

		mockMvc.perform(post("/products/save").contentType(MediaType.APPLICATION_JSON).content(requestJson))
				.andExpect(status().isBadRequest());

		assertEquals(0, productRepository.count());
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void shouldSaveAndFindProductById() throws Exception {

		String requestJson = """
				{
				  "name":"Laptop",
				  "description":"Gaming Laptop",
				  "price":50000,
				  "stockQuantity":10,
				  "category":"ELECTRONICS"
				}
				""";

		mockMvc.perform(post("/products/save").contentType(MediaType.APPLICATION_JSON).content(requestJson))
				.andExpect(status().isCreated());
		List<Product> products = productRepository.findAll();
		assertEquals(1, products.size());
		Long id = products.get(0).getId();
		Optional<Product> product = productRepository.findById(id);
		assertTrue(product.isPresent());
		assertEquals("Laptop", product.get().getName());
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void shouldGetProductById() throws Exception {

		String requestJson = """
				{
				  "name":"Laptop",
				  "description":"Gaming Laptop",
				  "price":50000,
				  "stockQuantity":10,
				  "category":"ELECTRONICS"
				}
				""";

		mockMvc.perform(post("/products/save").contentType(MediaType.APPLICATION_JSON).content(requestJson))
				.andExpect(status().isCreated());
		List<Product> products = productRepository.findAll();
		assertEquals(1, products.size());
		Long id = products.get(0).getId();
		mockMvc.perform(get("/products/{id}", id)).andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Laptop")).andExpect(jsonPath("$.price").value(50000.0));
	}

	@Test
	@WithMockUser(roles = "USER")
	void shouldSaveProduct() throws Exception {

		String requestJson = """
				{
				  "name":"Laptop",
				  "description":"Gaming Laptop",
				  "price":50000,
				  "stockQuantity":10,
				  "category":"ELECTRONICS"
				}
				""";

		ServletException ex = assertThrows(ServletException.class, () -> mockMvc
				.perform(post("/products/save").contentType(MediaType.APPLICATION_JSON).content(requestJson)));

		assertTrue(ex.getCause() instanceof AuthorizationDeniedException);

	}
	@Test
	@WithMockUser(roles = "ADMIN")
	void shouldSearchProducts() throws Exception {
		String requestJson1 = """
				{
				  "name":"Laptop",
				  "description":"Gaming Laptop",
				  "price":50000,
				  "stockQuantity":10,
				  "category":"ELECTRONICS"
				}
				""";

		mockMvc.perform(post("/products/save").contentType(MediaType.APPLICATION_JSON).content(requestJson1))
				.andExpect(status().isCreated());
		String requestJson2 = """
				{
				  "name":"Mobile",
				  "description":"Samsung",
				  "price":10000,
				  "stockQuantity":20,
				  "category":"ELECTRONICS"
				}
				""";

		mockMvc.perform(post("/products/save").contentType(MediaType.APPLICATION_JSON).content(requestJson2))
				.andExpect(status().isCreated());

		String requestJson3 = """
				{
				  "name":"LionKing",
				  "description":"Story Book",
				  "price":500,
				  "stockQuantity":100,
				  "category":"BOOKS"
				}
				""";

		mockMvc.perform(post("/products/save").contentType(MediaType.APPLICATION_JSON).content(requestJson3))
		.andExpect(status().isCreated());

		String requestJson4 = """
				{
				  "name":"Tab",
				  "description":"Ipad",
				  "price":40000,
				  "stockQuantity":15,
				  "category":"ELECTRONICS"
				}
				""";

		mockMvc.perform(post("/products/save").contentType(MediaType.APPLICATION_JSON).content(requestJson4))
				.andExpect(status().isCreated());
		List<Product> products = productRepository.findAll();
		System.out.println(
			    productRepository.findAll()
			);
		List<Product> books =
		        productRepository.findAll(
		                ProductSpecification.hasCategory("BOOKS")
		        );

		System.out.println("this is check for Book size"+books.size());
		System.out.println(books);
		assertEquals(4, products.size());

		assertEquals("Laptop", products.get(0).getName());
		mockMvc.perform(get("/products/search").param("category", "ELECTRONICS")).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.content").exists())
		.andExpect(jsonPath("$.content[0].name").value("Laptop")).andExpect(jsonPath("$.content[0].price").value(50000.0))
		.andExpect(jsonPath("$.content[1].name").value("Mobile")).andExpect(jsonPath("$.content[1].price").value(10000.0));
		mockMvc.perform(get("/products/search").param("category", "ELECTRONICS").param("minPrice", "1000")).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.content").exists());
		mockMvc.perform(get("/products/search").param("minPrice", "500").param("maxPrice", "50000")).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.content").exists());
		mockMvc.perform(get("/products/search").param("category", "BOOKS")).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.content").exists());
		mockMvc.perform(get("/products/search").param("name", "Lion")).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.content").exists());
		mockMvc.perform(get("/products/search").param("category", "ELECTRONICS").param("page", "0").param("size", "2").param("sort", "name,desc")).andDo(print())
		.andExpect(status().isOk()).andExpect(jsonPath("$.content").exists());
		
		
		mockMvc.perform(get("/products/searchByCb").param("category", "ELECTRONICS").param("minPrice", "1000")).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(3));

	}
	
	/*
	 * @Test
	 * 
	 * @WithMockUser(roles = "ADMIN") void shouldSaveProductToWishListTable() throws
	 * Exception {
	 * 
	 * String requestJson = """ { "name":"Laptop", "description":"Gaming Laptop",
	 * "price":50000, "stockQuantity":10, "category":"ELECTRONICS" } """;
	 * 
	 * mockMvc.perform(post("/products/save").contentType(MediaType.APPLICATION_JSON
	 * ).content(requestJson)) .andExpect(status().isCreated());
	 * 
	 * List<Product> products = productRepository.findAll();
	 * 
	 * assertEquals(1, products.size()); Long id = products.get(0).getId();
	 * assertEquals("Laptop", products.get(0).getName());
	 * mockMvc.perform(post("/wishlist/addProduct/1").contentType(MediaType.
	 * APPLICATION_JSON)).andExpect(status().isOk()); List<Wishlist> wishlists =
	 * wishlistRepository.findAll(); assertNotNull(wishlists); }
	 */
}
