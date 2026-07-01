package com.example.ecommerce.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.ecommerce.security.JwtAccessDeniedHandler;
import com.example.ecommerce.security.JwtAuthenticationEntryPoint;
import com.example.ecommerce.security.JwtAuthenticationFilter;
import com.example.ecommerce.security.RateLimitFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	
	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	
	private final JwtAuthenticationEntryPoint authenticationEntryPoint;

	private final JwtAccessDeniedHandler accessDeniedHandler;
	
	private final RateLimitFilter  rateLimitFilter;
	
	@Bean
	public SecurityFilterChain securityFilterChain(
	        HttpSecurity http) throws Exception {

	    http
	        .csrf(csrf -> csrf.disable())
	        .headers(headers ->
	        headers.frameOptions(
	            frame -> frame.disable()
	        )
	    )
	        .sessionManagement(session ->
            session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS)
	        )
	        .authorizeHttpRequests(auth -> auth
	                .requestMatchers(
	                		 "/auth/**",
	                	        "/swagger-ui/**",
	                	        "/v3/api-docs/**",
	                	        "/h2-console/**"
	                ).permitAll()
	                .anyRequest().authenticated()
	        ).addFilterBefore(
	        	    rateLimitFilter,
	        	    UsernamePasswordAuthenticationFilter.class
	        	)
	        	.addFilterAfter(
	        	    jwtAuthenticationFilter,
	        	    RateLimitFilter.class
	        	).exceptionHandling(ex -> ex
	        	    .authenticationEntryPoint(authenticationEntryPoint)
	        	    .accessDeniedHandler(accessDeniedHandler));

	    return http.build();
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
	    return new BCryptPasswordEncoder();
	}
	
	@Bean
	public AuthenticationManager authenticationManager(
	        AuthenticationConfiguration config)
	        throws Exception {

	    return config.getAuthenticationManager();
	}

}
