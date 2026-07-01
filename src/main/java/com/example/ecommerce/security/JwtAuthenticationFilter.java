package com.example.ecommerce.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.ecommerce.service.CustomUserDetailedService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtService jwtService;

	private final CustomUserDetailedService userDetailsService;
	
	
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		if (request.getServletPath().startsWith("/auth") || request.getServletPath().startsWith("/swagger-ui")
			    || request.getServletPath().startsWith("/v3/api-docs")
			    || request.getServletPath().startsWith("/h2-console")) {
		    filterChain.doFilter(request, response);
		    return;
		}
		final String authHeader =
		        request.getHeader("Authorization");
		if (authHeader == null ||
			    !authHeader.startsWith("Bearer ")) {

			    filterChain.doFilter(request, response);
			    return;
			}
		String jwtToken =
		        authHeader.substring(7);
		
		try {
			String username =
			        jwtService.extractUsername(jwtToken);
		if (username != null &&
			    SecurityContextHolder.getContext()
			            .getAuthentication() == null) {
			UserDetails userDetails =
			        userDetailsService
			                .loadUserByUsername(username);
			
			if (jwtService.isTokenValid(
			        jwtToken,
			        userDetails.getUsername())) {
				UsernamePasswordAuthenticationToken authToken =
				        new UsernamePasswordAuthenticationToken(

				                userDetails,
				                null,
				                userDetails.getAuthorities()
				        );
				authToken.setDetails(
				        new WebAuthenticationDetailsSource()
				                .buildDetails(request)
				);
				SecurityContextHolder.getContext()
		        .setAuthentication(authToken);
				
			}
			
			
			}
		}catch (io.jsonwebtoken.MalformedJwtException | io.jsonwebtoken.security.SignatureException | io.jsonwebtoken.ExpiredJwtException e) {
	        // Catch the exception and stop the filter chain right here
			System.out.println(e.getMessage());
	        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	        response.setContentType("application/json");
	        response.getWriter().write("{\"error\": \"Invalid or expired token\"}");
	        return; // Return early without calling filterChain.doFilter
	    }
		filterChain.doFilter(request, response);
	}

}
