package com.example.ecommerce.controller;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ecommerce.dto.AuthRequest;
import com.example.ecommerce.dto.AuthResponseDTO;
import com.example.ecommerce.dto.RefreshTokenRequestDTO;
import com.example.ecommerce.security.JwtService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthenticationManager
    authenticationManager;

	private final JwtService jwtService;
	
	@PostMapping("/login")
	public AuthResponseDTO login(
	        @RequestBody AuthRequest request) {

	    authenticationManager.authenticate(

	            new UsernamePasswordAuthenticationToken(
	                    request.getUsername(),
	                    request.getPassword()
	            )
	    );
	    
	    String accessToken =
	            jwtService.generateAccessToken(request.getUsername());

	    String refreshToken =
	            jwtService.generateRefreshToken(request.getUsername());

	    return new AuthResponseDTO(
	            accessToken,
	            refreshToken
	    );
	}
	
	@PostMapping("/refresh")
	public AuthResponseDTO refreshToken(
	        @RequestBody RefreshTokenRequestDTO  request) {
		 String refreshToken =
		            request.getRefreshToken();

		    String username =
		            jwtService.extractUsername(refreshToken);

		    if(username != null &&
		            jwtService.isTokenValid(
		                    refreshToken,
		                    username
		            )) {

		        String newAccessToken =
		                jwtService.generateAccessToken(
		                        username
		                );

		        return new AuthResponseDTO(
		                newAccessToken,
		                refreshToken
		        );
		    }

		    throw new RuntimeException(
		            "Invalid Refresh Token"
		    );
	}
}
