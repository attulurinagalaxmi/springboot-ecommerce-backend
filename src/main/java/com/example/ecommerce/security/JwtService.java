package com.example.ecommerce.security;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
	
	private static final String SECRET_KEY =
	        "myverysecuresecretkeyforjwtauthentication12345";
	
	public String generateAccessToken(String username) {

	    return Jwts.builder()
	            .subject(username)

	            .issuedAt(new Date())

	            .expiration(new Date(
	                    System.currentTimeMillis()
	                    + 1000 * 60 * 15
	            ))

	            .signWith(getSignKey())

	            .compact();
	}
	
	public String generateRefreshToken(String username) {

	    return Jwts.builder()
	            .subject(username)

	            .issuedAt(new Date())

	            .expiration(new Date(
	                    System.currentTimeMillis()
	                    + 1000L * 60 * 60 * 24 * 7
	            ))

	            .signWith(getSignKey())

	            .compact();
	}
	
	private Key getSignKey() {

	    byte[] keyBytes =
	            Decoders.BASE64.decode(SECRET_KEY);

	    return Keys.hmacShaKeyFor(keyBytes);
	}

	public String extractUsername(String token) {

	    return extractClaim(token, Claims::getSubject);
	}
	
	public <T> T extractClaim(
	        String token,
	        Function<Claims, T> claimsResolver) {

	    final Claims claims =
	            extractAllClaims(token);

	    return claimsResolver.apply(claims);
	}
	
	private Claims extractAllClaims(String token) {

	    return Jwts.parser()
	            .verifyWith((SecretKey) getSignKey())
	            .build()
	            .parseSignedClaims(token)
	            .getPayload();
	}
	
	public boolean isTokenValid(
	        String token,
	        String username) {

	    final String extractedUsername =
	            extractUsername(token);

	    return extractedUsername.equals(username)
	            && !isTokenExpired(token);
	}
	
	private boolean isTokenExpired(String token) {

	    return extractClaim(
	            token,
	            Claims::getExpiration)
	            .before(new Date());
	}
}
