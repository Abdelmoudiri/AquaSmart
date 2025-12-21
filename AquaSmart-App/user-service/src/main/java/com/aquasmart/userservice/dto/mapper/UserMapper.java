package com.aquasmart.userservice.dto.mapper;

import com.aquasmart.userservice.dto.response.UserDTO;
import com.aquasmart.userservice.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Mapper pour convertir User en UserDTO
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDTO toDTO(User user);

    List<UserDTO> toDTOList(List<User> users);
}
