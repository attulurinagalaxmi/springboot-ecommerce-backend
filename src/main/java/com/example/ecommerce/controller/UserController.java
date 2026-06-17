package com.example.ecommerce.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.ecommerce.dto.CreateUserRequest;
import com.example.ecommerce.dto.UserResponseDTO;
import com.example.ecommerce.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "User APIs", description = "Operations related to users")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class UserController {

	private final UserService service;

	Authentication authentication;

	@GetMapping("/me")
	public String currentUser(Authentication authentication) {

		return authentication.getName();
	}

	@GetMapping("/hello")
	public String sayHello() {
		return "Hello Naga";
	}

	@Operation(summary = "Save User")
	@PostMapping("/save")
	public ResponseEntity<UserResponseDTO> saveUser(@Valid @RequestBody CreateUserRequest userDto) {
		UserResponseDTO dto = service.saveUser(userDto);
		return new ResponseEntity<>(dto, HttpStatus.CREATED);

	}

	@Operation(summary = "Get all users")
	@GetMapping
	public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
		return ResponseEntity.ok(service.getAllUsers());
	}

	@Operation(summary = "Get User by Id")
	@GetMapping("/{id}")
	public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
		return ResponseEntity.ok(service.getUserById(id));
	}

	@Operation(summary = "Get User by Email Id")
	@GetMapping("/email")
	public ResponseEntity<UserResponseDTO> getUserByEmail(@RequestParam String email) {

		return ResponseEntity.ok(service.getUserByEmail(email));
	}

	@Operation(summary = "Search users by Name")
	@GetMapping("/search")
	public ResponseEntity<List<UserResponseDTO>> searchUsers(@RequestParam String name) {

		return ResponseEntity.ok(service.searchUsersByName(name));
	}

	@Operation(summary = "Get Users with Pagination")
	@GetMapping("/paged")
	public ResponseEntity<Page<UserResponseDTO>> getUsersWithPagination(

			@RequestParam(defaultValue = "0") int page,

			@RequestParam(defaultValue = "5") int size,

			@RequestParam(defaultValue = "name") String sortBy) {

		return ResponseEntity.ok(service.getUsersWithPagination(page, size, sortBy));
	}

	@Operation(summary = "Update User by Id")
	@PutMapping("/{id}")
	public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id,
			@Valid @RequestBody CreateUserRequest userDTO) {

		return ResponseEntity.ok(service.updateUser(id, userDTO));
	}

	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Delete User by Id")
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteUser(@PathVariable Long id) {
		service.deleteUser(id);
		return ResponseEntity.ok("User deleted successfully");
	}

	/*
	 * @Operation(summary = "Create order for User")
	 * 
	 * @PostMapping("/{userId}/orders") public ResponseEntity<OrderDTO>
	 * createOrder(@PathVariable Long userId, @RequestBody OrderDTO order) {
	 * 
	 * return new ResponseEntity<>(service.createOrder(userId, order),
	 * HttpStatus.CREATED); }
	 */

}
