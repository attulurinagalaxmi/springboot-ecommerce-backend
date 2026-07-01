package com.example.ecommerce.security;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.ecommerce.service.CustomUserDetailedService;
import com.example.ecommerce.service.RateLimitService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter{

	private final static String SC_TOO_MANY_REQUESTS = "too many requests";
    private final RateLimitService rateLimitService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();
        System.out.println("RateLimitFilter executed");
        if ("/auth/login".equals(uri)
                && request.getMethod().equalsIgnoreCase("POST")) {

            String ip = request.getRemoteAddr();
            System.out.println("IP = " + ip);
            if (!rateLimitService.isAllowed(ip)) {

            	System.out.println(
            		    "Allowed = " + rateLimitService.isAllowed(ip)
            		);
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());

                response.setContentType("application/json");

                response.getWriter().write(
                        """
                        {
                           "message":"Too many login attempts. Try again later."
                        }
                        """
                );

                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
