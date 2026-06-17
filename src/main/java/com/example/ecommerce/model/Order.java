package com.example.ecommerce.model;

import java.sql.Timestamp;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	//private String productName;	
	private Double totalAmount;
	
	@JsonBackReference
	@ManyToOne(fetch= FetchType.LAZY)
	@JoinColumn(name ="user_id")
	private User user;
	private Timestamp createdAt;
	
	@OneToMany(
	        mappedBy = "order",
	        cascade = CascadeType.ALL, fetch= FetchType.LAZY
	)
	private List<OrderItem> orderItems;

}
