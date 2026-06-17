package com.example.ecommerce.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.ecommerce.dto.CreateUserRequest;
import com.example.ecommerce.dto.UserResponseDTO;
import com.example.ecommerce.exception.UserNotFoundException;
import com.example.ecommerce.mapper.UserMapper;
import com.example.ecommerce.model.Role;
import com.example.ecommerce.model.User;
import com.example.ecommerce.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;

	private final UserMapper userMapper;
	
	private final PasswordEncoder passwordEncoder;

	public UserResponseDTO saveUser(CreateUserRequest userDTO) {

		log.info("Saving user ");
		User user = userMapper.toEntity(userDTO);
		user.setPassword(
			    passwordEncoder.encode(userDTO.getPassword())
			);

		user.setRole(Role.USER);
		User savedUser = userRepository.save(user);

		UserResponseDTO response = userMapper.toDTO(savedUser);
		return response;
	}

	public List<UserResponseDTO> getAllUsers() {
		return userRepository.findUsersWithOrders().stream().map(userMapper::toDTO).collect(Collectors.toList());
	}

	public Page<UserResponseDTO> getUsersWithPagination(int page, int size, String sortBy) {

		Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());

		Page<User> userPage = userRepository.findAll(pageable);

		return userPage.map(userMapper::toDTO);
	}

	public UserResponseDTO getUserByEmail(String email) {

		User user = userRepository.getUserUsingJPQL(email)
				.orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

		UserResponseDTO dto = userMapper.toDTO(user);

		return dto;
	}

	public List<UserResponseDTO> searchUsersByName(String name) {

		return userRepository.findByNameContaining(name).stream().map(userMapper::toDTO).toList();
	}

	public UserResponseDTO getUserById(Long id) {

		User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));

		UserResponseDTO dto = userMapper.toDTO(user);

		return dto;
	}

	public UserResponseDTO updateUser(Long id, CreateUserRequest userDTO) {

		User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));

		userMapper.updateUserFromDto(userDTO, user);

		User updatedUser = userRepository.save(user);

		UserResponseDTO dto = userMapper.toDTO(updatedUser);

		return dto;
	}

	public String deleteUser(Long id) {

		User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));

		userRepository.delete(user);

		return "User deleted successfully";
	}

	/*
	 * public OrderDTO createOrder(Long userId, OrderDTO orderDto) { User user =
	 * userRepository.findById(userId).orElseThrow(() -> new
	 * UserNotFoundException("User not found")); Order order = new Order();
	 * order.setProductName(orderDto.getProductName());
	 * order.setPrice(orderDto.getPrice()); order.setUser(user); //Order order =
	 * orderMapper.toEntity(orderDto, user); Order savedOrder =
	 * orderRepository.save(order); return orderMapper.toDTO(savedOrder); }
	 */

}
