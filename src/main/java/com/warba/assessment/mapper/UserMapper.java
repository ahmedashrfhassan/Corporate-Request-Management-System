package com.warba.assessment.mapper;

import com.warba.assessment.dto.request.CreateUserDto;
import com.warba.assessment.dto.response.UserDto;
import com.warba.assessment.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(CreateUserDto createUserDTO);

    UserDto convertToDTO(User user);

}
