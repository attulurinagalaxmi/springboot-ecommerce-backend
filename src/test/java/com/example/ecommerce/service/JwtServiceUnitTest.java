package com.example.ecommerce.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.ecommerce.security.JwtService;

@ExtendWith(MockitoExtension.class)
public class JwtServiceUnitTest {

	@InjectMocks
	private JwtService jwtService;

	@Test
	void shouldGenerateAndValidateToken() {
		String token = jwtService.generateAccessToken("user@gmail.com");

		String username = jwtService.extractUsername(token);

		assertEquals("user@gmail.com", username);
		assertTrue(jwtService.isTokenValid(token, "user@gmail.com"));

	}

}
