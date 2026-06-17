package com.example.ecommerce;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class EcommerceApplication {

	public static void main(String[] args) {
		 TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));

	        System.out.println("Current JVM TimeZone: "
	                + TimeZone.getDefault().getID());
		SpringApplication.run(EcommerceApplication.class, args);
	}

}
