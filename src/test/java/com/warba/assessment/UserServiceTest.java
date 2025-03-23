package com.warba.assessment;

import com.warba.assessment.builder.UserDtos;
import com.warba.assessment.builder.Users;
import com.warba.assessment.dto.request.CreateUserDto;
import com.warba.assessment.dto.request.UpdateUserDto;
import com.warba.assessment.dto.response.UserDto;
import com.warba.assessment.entity.User;
import com.warba.assessment.exception.BusinessValidationException;
import com.warba.assessment.exception.ResourceNotFoundException;
import com.warba.assessment.mapper.UserMapper;
import com.warba.assessment.repository.UserRepository;
import com.warba.assessment.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static com.warba.assessment.builder.CreateUserDtos.createUserDtoBuilder;
import static com.warba.assessment.builder.Users.userBuilder;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserMapper userMapper;
    private User testUser;
    private UserDto testUserDto;
    private CreateUserDto testCreateUserDto;

    @BeforeEach
    void setUp() {
        testUser = userBuilder().build();
        testUserDto = UserDtos.userBuilder().build();
        testCreateUserDto = createUserDtoBuilder().build();
    }

    @Test
    void getUserById_WhenUserExists_ShouldReturnUser() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userMapper.convertToDTO(testUser)).thenReturn(testUserDto);

        // Act
        UserDto result = userService.getUserById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testUserDto.getId(), result.getId());
        assertEquals(testUserDto.getName(), result.getName());
        verify(userRepository).findById(1L);
    }

    @Test
    void getUserById_WhenUserDoesNotExist_ShouldThrowException() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(1L));
        verify(userRepository).findById(1L);
    }

    @Test
    void createUser_WithValidData_ShouldReturnCreatedUser() {
        // Arrange
        when(userRepository.findByCivilId(anyString())).thenReturn(Optional.empty());
        when(userMapper.toEntity(any(CreateUserDto.class))).thenReturn(testUser);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        Long id = userService.createUser(testCreateUserDto);

        // Assert
        assertNotNull(id);
        verify(userRepository).findByCivilId(anyString());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_WithDuplicateCivilId_ShouldThrowException() {
        // Arrange
        when(userRepository.findByCivilId(anyString())).thenReturn(Optional.of(testUser));

        // Act & Assert
        assertThrows(BusinessValidationException.class, () -> userService.createUser(testCreateUserDto));
        verify(userRepository).findByCivilId(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_WhenUserExists_ShouldReturnUpdatedUser() {
        // Arrange
        User updatedUser = Users.userBuilder().build();

        UpdateUserDto updateDTO = UpdateUserDto.builder()
                .name("Updated Name")
                .expiryDate(LocalDate.now().plusYears(2))
                .build();

        when(userRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        // Act
        Boolean updated = userService.updateUser(1L, updateDTO);

        // Assert
        assertNotNull(updated);
        verify(userRepository).findByIdAndDeletedFalse(1L);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void deleteUser_WhenUserExists_ShouldDeleteSuccessfully() {
        // Arrange
        when(userRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(testUser));

        // Act
        userService.deleteUser(1L);

        // Assert
        assertNotNull(testUser.getDeleted());
        assertTrue(testUser.getDeleted());
        verify(userRepository).findByIdAndDeletedFalse(1L);
    }

    @Test
    void deleteUser_WhenUserDoesNotExist_ShouldThrowException() {
        // Arrange
        when(userRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(1L));
        verify(userRepository).findByIdAndDeletedFalse(1L);
    }

    @Test
    void isCivilIdExpired_WhenExpired_ShouldReturnTrue() {
        // Arrange
        User expiredUser = Users.expiredUserBuilder().build();

        // Act
        boolean result = expiredUser.isCivilIdExpired();

        // Assert
        assertTrue(result);
    }
}