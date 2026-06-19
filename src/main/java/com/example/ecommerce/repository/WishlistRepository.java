package com.example.ecommerce.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.ecommerce.model.Wishlist;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long>{

	Optional<Wishlist> findByUserId(Long userId);
	
	 // Using FETCH JOIN avoids the "N+1 select problem" and loads products instantly
    @Query("SELECT w FROM Wishlist w LEFT JOIN FETCH w.products WHERE w.user.id = :userId")
    Optional<Wishlist> findByUserIdWithProducts(@Param("userId") Long userId);
}
