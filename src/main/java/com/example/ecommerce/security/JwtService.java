package com.example.ecommerce.security;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import com.example.ecommerce.model.RefreshToken;
import com.example.ecommerce.repository.RefreshTokenRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtService {

	private static final String SECRET_KEY = "myverysecuresecretkeyforjwtauthentication12345";

	private final RefreshTokenRepository refreshTokenRepository;

	public String generateAccessToken(String username) {

		return Jwts.builder().subject(username)
				.id(UUID.randomUUID().toString())

				.issuedAt(new Date())

				.expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 10))

				.signWith(getSignKey())

				.compact();
	}

	public String generateRefreshToken(String username) {

		return Jwts.builder().subject(username)
				.id(UUID.randomUUID().toString())

				.issuedAt(new Date())

				.expiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 7))

				.signWith(getSignKey())

				.compact();
	}

	private Key getSignKey() {

		byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);

		return Keys.hmacShaKeyFor(keyBytes);
	}

	public String extractUsername(String token) {

		return extractClaim(token, Claims::getSubject);
	}

	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {

		final Claims claims = extractAllClaims(token);

		return claimsResolver.apply(claims);
	}

	private Claims extractAllClaims(String token) {

		return Jwts.parser().verifyWith((SecretKey) getSignKey()).build().parseSignedClaims(token).getPayload();
	}

	public RefreshToken validateRefreshToken(String token) {

		RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
				.orElseThrow(() -> new RuntimeException("Refresh token not found"));

		if (refreshToken.isRevoked()) {
			throw new RuntimeException("Refresh token revoked");
		}

		if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
			throw new RuntimeException("Refresh token expired");
		}

		return refreshToken;
	}

	public boolean isTokenValid(String token, String username) {

		final String extractedUsername = extractUsername(token);

		return extractedUsername.equals(username) && !isTokenExpired(token);
	}

	private boolean isTokenExpired(String token) {

		return extractClaim(token, Claims::getExpiration).before(new Date());
	}
}
