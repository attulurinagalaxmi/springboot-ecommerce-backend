package com.example.ecommerce.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.example.ecommerce.dto.RefreshTokenRequestDTO;
import com.example.ecommerce.model.RefreshToken;
import com.example.ecommerce.repository.RefreshTokenRepository;
import com.jayway.jsonpath.JsonPath;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthenticationLoginIntegrationTest {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private RefreshTokenRepository refreshRepository;

	@Test
	void shouldTestLogin() throws Exception {
		String requestJson = """
				{
					"username": "admin@gmail.com",
					"password": "admin123"
				}
				""";
		MvcResult mvcResult = mockMvc
				.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(requestJson))
				.andExpect(status().isOk()).andExpect(jsonPath("$.accessToken").exists()).andReturn();

		String responseString = mvcResult.getResponse().getContentAsString();
		// 3. Parse the specific 'accessToken' field from the JSON string
		String token = JsonPath.read(responseString, "$.accessToken");

		mockMvc.perform(get("/products").header("Authorization", "Bearer " + token)).andExpect(status().isOk());
	}
	
	@Test
	void shouldTestLoginWithRefreshToken() throws Exception {
		String requestJson = """
				{
					"username": "admin@gmail.com",
					"password": "admin123"
				}
				""";
		MvcResult mvcResult = mockMvc
				.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(requestJson))
				.andExpect(status().isOk()).andExpect(jsonPath("$.accessToken").exists()).andReturn();

		String responseString = mvcResult.getResponse().getContentAsString();
		// 3. Parse the specific 'accessToken' field from the JSON string
		String token = JsonPath.read(responseString, "$.accessToken");
		String refreshToken = JsonPath.read(responseString, "$.refreshToken");
		System.out.println("refreshToken :: "+refreshToken);
		String req = "{\"refreshToken\": \"" + refreshToken + "\"}";
		Optional<RefreshToken> refreshEntity = refreshRepository.findByToken(refreshToken);
		assertTrue(refreshEntity.isPresent());
		assertEquals("admin@gmail.com", refreshEntity.get().getUser().getEmail());
		System.out.println(refreshEntity);
		mockMvc.perform(get("/products").header("Authorization", "Bearer " + token)).andExpect(status().isOk());
		MvcResult mvcNewResult = mockMvc
				.perform(post("/auth/refresh").contentType(MediaType.APPLICATION_JSON).content(req))
				.andExpect(status().isOk()).andExpect(jsonPath("$.accessToken").exists()).andExpect(jsonPath("$.refreshToken").exists()).andReturn();
		String responseStringNew = mvcNewResult.getResponse().getContentAsString();
		// 3. Parse the specific 'accessToken' field from the JSON string
		String newToken = JsonPath.read(responseStringNew, "$.accessToken");
		mockMvc.perform(get("/products").header("Authorization", "Bearer " + newToken)).andExpect(status().isOk());
	}
	@Test
	void shouldTestLogoutWithRefreshToken() throws Exception {
		String requestJson = """
				{
					"username": "admin@gmail.com",
					"password": "admin123"
				}
				""";
		MvcResult mvcResult = mockMvc
				.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(requestJson))
				.andExpect(status().isOk()).andExpect(jsonPath("$.accessToken").exists()).andReturn();

		String responseString = mvcResult.getResponse().getContentAsString();
		// 3. Parse the specific 'accessToken' field from the JSON string
		String token = JsonPath.read(responseString, "$.accessToken");
		String refreshToken = JsonPath.read(responseString, "$.refreshToken");
		System.out.println("refreshToken :: "+refreshToken);
		String req = "{\"refreshToken\": \"" + refreshToken + "\"}";
		mockMvc.perform(post("/auth/logout").contentType(MediaType.APPLICATION_JSON).content(req))
				.andExpect(status().isOk()).andReturn();
		Optional<RefreshToken> refreshEntity = refreshRepository.findByToken(refreshToken);
		assertTrue(refreshEntity.isPresent());
		assertTrue(refreshEntity.get().isRevoked());
		mockMvc.perform(post("/auth/refresh").contentType(MediaType.APPLICATION_JSON).content(req))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void shouldTestLoginWithInvalidToken() throws Exception {
		String requestJson = """
				{
					"username": "admin@gmail.com",
					"password": "admin123"
				}
				""";
		MvcResult mvcResult = mockMvc
				.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(requestJson))
				.andExpect(status().isOk()).andExpect(jsonPath("$.accessToken").exists()).andReturn();

		String responseString = mvcResult.getResponse().getContentAsString();
		// 3. Parse the specific 'accessToken' field from the JSON string
		String token = JsonPath.read(responseString, "$.accessToken");

		mockMvc.perform(get("/products").header("Authorization", "Bearer aaaaa.bbbbb.ccccc"))
				.andExpect(status().isUnauthorized());
	}

}
