package com.example.ecommerce.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.example.ecommerce.dto.CreateUserRequest;
import com.example.ecommerce.dto.UserResponseDTO;
import com.example.ecommerce.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
	
	UserResponseDTO toDTO(User user);
	
	@Mapping(target = "password", ignore = true)
	@Mapping(target = "role", ignore = true)
	User toEntity(CreateUserRequest dto);
	
	void updateUserFromDto(
			CreateUserRequest dto,
            @MappingTarget User user
    );

}
