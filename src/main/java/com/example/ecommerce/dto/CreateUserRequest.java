package com.example.ecommerce.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {
	
	@Schema(example = "Alice")
	@NotBlank(message = "Name is required")
	private String name;
	@Schema(example = "alice@gmail.com")
	@Email(message = "Invalid email")
	@NotBlank(message = "Email is required")
	private String email;
	@NotBlank(message = "Password is required")
	@Size(min = 6)
	private String password;
	
}
