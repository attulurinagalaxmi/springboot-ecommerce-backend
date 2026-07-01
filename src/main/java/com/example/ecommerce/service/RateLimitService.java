package com.example.ecommerce.service;

import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RateLimitService {
	
	  private final StringRedisTemplate redisTemplate;
	  
	  private final static Long MAX_ATTEMPTS = 5l;
	  
	  public boolean isAllowed(String ip){
		
		  String key = "login_attempts:" + ip;

		  Long attempts =
		          redisTemplate.opsForValue().increment(key);

		  if(attempts!= null && attempts == 1){
		      redisTemplate.expire(key, Duration.ofMinutes(1));
		  }
		  return attempts != null && attempts <= MAX_ATTEMPTS;
	  }
}
