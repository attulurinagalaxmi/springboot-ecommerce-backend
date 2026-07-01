package com.example.ecommerce.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.ecommerce.dto.ProductResponseDTO;
import com.example.ecommerce.dto.WishlistResponseDTO;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.mapper.ProductMapper;
import com.example.ecommerce.model.Audit;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.model.User;
import com.example.ecommerce.model.Wishlist;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.repository.WishlistRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
@Transactional
public class WishlistService {

	private final WishlistRepository wishlistRepository;
	private final ProductRepository productRepository;
	private final UserRepository userRepository;
	private final ProductMapper productMapper;

	@Audit(action = "ADD_TO_WISHLIST")
	public void addProductToWishlist(Long productId) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		String email = authentication.getName();
		User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
		Wishlist wishlist = wishlistRepository.findByUserId(user.getId()).orElseGet(() -> {
			Wishlist newWishlist = new Wishlist();
			newWishlist.setUser(user);
			newWishlist.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
			// Save the new wishlist first so it gets an ID
			return wishlistRepository.save(newWishlist);
		});

		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));

		// Prevent adding duplicates to the list
		// since collection is we don't need this check anymore
		// if (!wishlist.getProducts().contains(product)) {
		wishlist.getProducts().add(product);
		// JPA automatically inserts a row into the 'wishlist_product' join table here
		// }
	}

	@Audit(action = "REMOVE_FROM_WISHLIST")
	public void removeProductFromWishlist(Long productId) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		String email = authentication.getName();
		User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
		Wishlist wishlist = wishlistRepository.findByUserId(user.getId())
				.orElseThrow(() -> new ResourceNotFoundException("Wishlist not found for user: " + user.getId()));

		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));

		wishlist.getProducts().remove(product);
		// JPA automatically deletes the row from the 'wishlist_product' join table here
	}

	public WishlistResponseDTO getUserWishlistProducts() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		String email = authentication.getName();
		User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));

		// Fetch the wishlist using our optimized query
		Wishlist wishlist = wishlistRepository.findByUserIdWithProducts(user.getId())
				.orElseThrow(() -> new ResourceNotFoundException("Wishlist empty or not found for user: " + user.getId()));

		// This contains all your products ready to be sent to the controller!
		WishlistResponseDTO wishlistResponseDTO = convertProductToDTO(wishlist.getProducts());
		wishlistResponseDTO.setUserEmail(email);
		wishlistResponseDTO.setCreatedAt(wishlist.getCreatedAt());
		return wishlistResponseDTO;
	}

	private WishlistResponseDTO convertProductToDTO(Set<Product> products) {
		WishlistResponseDTO response = new WishlistResponseDTO();
		List<ProductResponseDTO> productResponse = new ArrayList<>();
		for (Product product : products) {
			ProductResponseDTO dto = productMapper.toDTO(product);
			productResponse.add(dto);
		}
		response.setProducts(productResponse);
		return response;
	}
}
