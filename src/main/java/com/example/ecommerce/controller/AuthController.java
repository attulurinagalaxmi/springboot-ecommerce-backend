package com.example.ecommerce.controller;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ecommerce.dto.AuthRequest;
import com.example.ecommerce.dto.AuthResponseDTO;
import com.example.ecommerce.dto.RefreshTokenRequestDTO;
import com.example.ecommerce.exception.UserNotFoundException;
import com.example.ecommerce.model.RefreshToken;
import com.example.ecommerce.model.User;
import com.example.ecommerce.repository.RefreshTokenRepository;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.security.JwtService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

	private final AuthenticationManager authenticationManager;

	private final JwtService jwtService;

	private final RefreshTokenRepository refreshTokenRepository;
	private final UserRepository userRepository;

	@PostMapping("/login")
	public AuthResponseDTO login(@RequestBody AuthRequest request) {

		authenticationManager.authenticate(

				new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

		String accessToken = jwtService.generateAccessToken(request.getUsername());

		String refreshToken = jwtService.generateRefreshToken(request.getUsername());

		RefreshToken refreshTokenEntity = new RefreshToken();
		User user = userRepository.findByEmail(request.getUsername())
				.orElseThrow(() -> new UserNotFoundException("User not found"));

		refreshTokenEntity.setToken(refreshToken);
		refreshTokenEntity.setUser(user);
		refreshTokenEntity.setExpiresAt(LocalDateTime.now().plusDays(30));

		refreshTokenRepository.save(refreshTokenEntity);

		return new AuthResponseDTO(accessToken, refreshToken);
	}

	@PostMapping("/refresh")
	public AuthResponseDTO refreshToken(@RequestBody RefreshTokenRequestDTO request) {
		String refreshToken = request.getRefreshToken();

		String username = jwtService.extractUsername(refreshToken);
		
		if (!jwtService.isTokenValid(refreshToken, username)) {
			throw new RuntimeException("Invalid Refresh token ");
		}

		RefreshToken refreshTokenEntity = jwtService.validateRefreshToken(refreshToken);

		if (refreshTokenEntity.getExpiresAt().isBefore(LocalDateTime.now())) {

			refreshTokenEntity.setRevoked(true);

			refreshTokenRepository.save(refreshTokenEntity);

			throw new RuntimeException("Refresh token expired");
		}

		String newAccessToken = jwtService.generateAccessToken(username);

		return new AuthResponseDTO(newAccessToken, refreshToken);

	}

	@PostMapping("/logout")
	public ResponseEntity<String> logout(@RequestBody RefreshTokenRequestDTO request) {
		log.info("Logout of the user");
		RefreshToken refreshTokenEntity = refreshTokenRepository.findByToken(request.getRefreshToken())
				.orElseThrow(() -> new RuntimeException("Refresh token not found"));
		refreshTokenEntity.setRevoked(true);
		refreshTokenRepository.save(refreshTokenEntity);
		return ResponseEntity.ok("User logged out successfully");
	}
}
