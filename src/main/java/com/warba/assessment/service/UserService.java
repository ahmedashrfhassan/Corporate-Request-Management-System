package com.warba.assessment.service;

import com.warba.assessment.dto.request.CreateUserDto;
import com.warba.assessment.dto.request.UpdateUserDto;
import com.warba.assessment.dto.response.UserDto;
import com.warba.assessment.entity.User;
import com.warba.assessment.exception.BusinessValidationException;
import com.warba.assessment.mapper.UserMapper;
import com.warba.assessment.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.warba.assessment.exception.Messages.USER_NOT_FOUND;
import static com.warba.assessment.exception.suppliers.ResourceNotFoundSupplier.entityNotFoundSupplier;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(entityNotFoundSupplier(USER_NOT_FOUND.evaluated(id)));
        return userMapper.convertToDTO(user);
    }

    @Transactional
    public Long createUser(CreateUserDto dto) {
        Optional<User> userOp = userRepository.findByCivilId(dto.getCivilId());
        User user;
        if (userOp.isPresent()) {
            user = userOp.get();
            if (user.getDeleted()) {
                returnExistingDeletedUserToSystem(dto, user);
                return userRepository.save(user).getId();
            } else {
                throw new BusinessValidationException("Civil ID already exists");
            }
        } else {
            user = userMapper.toEntity(dto);
            return userRepository.save(user).getId();
        }

    }

    private static void returnExistingDeletedUserToSystem(CreateUserDto dto, User user) {
        user.setDeleted(false);
        user.setName(dto.getName());
        user.setExpiryDate(dto.getExpiryDate());
    }

    @Transactional
    public Boolean updateUser(Long id, UpdateUserDto dto) {
        User existingUser = userRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(entityNotFoundSupplier(USER_NOT_FOUND.evaluated(id)));

        existingUser.setName(dto.getName());
        existingUser.setExpiryDate(dto.getExpiryDate());

        userRepository.save(existingUser);
        return true;
    }

    @Transactional
    public void deleteUser(Long id) {
        User existingUser = userRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(entityNotFoundSupplier(USER_NOT_FOUND.evaluated(id)));

        existingUser.setDeleted(true);
        userRepository.save(existingUser);
    }

}