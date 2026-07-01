package com.example.ecommerce.config;

import java.time.LocalDateTime;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.example.ecommerce.model.Audit;
import com.example.ecommerce.model.AuditLog;
import com.example.ecommerce.repository.AuditLogRepository;

import lombok.RequiredArgsConstructor;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

	private final AuditLogRepository auditLogRepository;

	@AfterReturning("@annotation(audit)")
	public void auditMethod(Audit audit) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = "SYSTEM";

		if (authentication != null && authentication.isAuthenticated()
				&& !"anonymousUser".equals(authentication.getPrincipal())) {

			username = authentication.getName();
		}
		AuditLog log = new AuditLog();
		log.setUsername(username);
		log.setAction(audit.action());

		log.setCreatedAt(LocalDateTime.now());

		auditLogRepository.save(log);
	}

}
