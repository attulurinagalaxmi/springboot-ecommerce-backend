package com.example.ecommerce.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.ecommerce.model.Role;
import com.example.ecommerce.model.User;
import com.example.ecommerce.repository.UserRepository;

@Configuration
public class DataInitializationClass {
	
	@Bean
	CommandLineRunner initUsers(
	        UserRepository userRepository,
	        PasswordEncoder passwordEncoder) {

	    return args -> {

	        if(userRepository.count() == 0) {

	            User admin = new User();

	            admin.setName("Admin");

	            admin.setEmail("admin@gmail.com");

	            admin.setPassword(
	                    passwordEncoder.encode("admin123")
	            );

	            admin.setRole(Role.ADMIN);

	            userRepository.save(admin);

	            User user = new User();

	            user.setName("User");

	            user.setEmail("user@gmail.com");

	            user.setPassword(
	                    passwordEncoder.encode("user123")
	            );

	            user.setRole(Role.USER);

	            userRepository.save(user);
	        }
	    };
	}

}
