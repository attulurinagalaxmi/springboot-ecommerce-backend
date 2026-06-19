package com.example.ecommerce.model;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Wishlist {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch= FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;
	
	 @ManyToMany(fetch = FetchType.LAZY)
	 @JoinTable(
	     name = "wishlist_product", // Name of the join table
	     joinColumns = @JoinColumn(name = "wishlist_id"),
	     inverseJoinColumns = @JoinColumn(name = "product_id")
	 )
	 private Set<Product> products = new HashSet<>();
	
	 private Timestamp createdAt;
	
}
