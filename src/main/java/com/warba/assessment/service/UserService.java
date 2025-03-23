package com.warba.assessment.service;

import com.warba.assessment.dto.request.CreateUserDto;
import com.warba.assessment.dto.request.UpdateUserDto;
import com.warba.assessment.dto.response.UserDto;

public interface UserService {
    UserDto getUserById(Long id);

    Long createUser(CreateUserDto dto);

    Boolean updateUser(Long id, UpdateUserDto dto);

    void deleteUser(Long id);
}
