package com.example.ecommerce.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.jayway.jsonpath.JsonPath;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthenticationLoginIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

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

		mockMvc.perform(get("/products").header("Authorization", "Bearer aaaaa.bbbbb.ccccc")).andExpect(status().isUnauthorized());
	}
	
}
