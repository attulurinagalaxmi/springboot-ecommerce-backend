package com.example.ecommerce.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ecommerce.dto.WishlistResponseDTO;
import com.example.ecommerce.service.WishlistService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@Tag(name = "WishList APIs", description = "Operations related to wishlist products")
@RestController
@RequestMapping("/wishlist")
@AllArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class WishlistController {
	
	private WishlistService wishlistService;
	
	@Operation(summary = "Add product to wishlist")
	@PostMapping("/addProduct/{id}")
	public ResponseEntity<String> addToWishList(@PathVariable("id") Long id){
		
		wishlistService.addProductToWishlist(id);
		return ResponseEntity.ok("Product successfully added to wishlist");
	}
	
	@Operation(summary = "delete product to wishlist")
	@DeleteMapping("/removeProduct/{id}")
	public ResponseEntity<String> removeFromWishList(@PathVariable("id") Long id){
		
		wishlistService.removeProductFromWishlist(id);
		return ResponseEntity.ok("Product successfully removed from wishlist");
	}
	
	@Operation(summary = "get user wishlisted products")
	@GetMapping("/allItems")
	public ResponseEntity<WishlistResponseDTO> getUserWishlistedProducts(){
		return ResponseEntity.ok(wishlistService.getUserWishlistProducts());
	}
}
