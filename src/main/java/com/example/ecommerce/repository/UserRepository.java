package com.example.ecommerce.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.ecommerce.model.User;

public interface UserRepository extends JpaRepository<User, Long>{
	
	
	@Query("""
		    SELECT DISTINCT u
		    FROM User u
		    JOIN FETCH u.orders
		""")
	 List<User> findUsersWithOrders();
	
	 Optional<User> findByEmail(String email);
	 
	 List<User> findByNameContaining(String name);
	 
	 @Query("SELECT u FROM User u WHERE u.email = :email")
	 Optional<User> getUserUsingJPQL(@Param("email") String email);

}
