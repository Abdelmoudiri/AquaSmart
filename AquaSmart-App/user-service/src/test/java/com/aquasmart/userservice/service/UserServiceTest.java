package com.aquasmart.userservice.service;

import com.aquasmart.userservice.dto.mapper.UserMapper;
import com.aquasmart.userservice.dto.response.UserDTO;
import com.aquasmart.userservice.exception.UserNotFoundException;
import com.aquasmart.userservice.model.User;
import com.aquasmart.userservice.model.enums.Role;
import com.aquasmart.userservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    void getUserById_ShouldReturnUser_WhenUserExists() {
        // Arrange
        String userId = "123";
        User user = User.builder().id(userId).email("test@test.com").build();
        UserDTO userDTO = UserDTO.builder().id(userId).email("test@test.com").build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toDTO(user)).thenReturn(userDTO);

        // Act
        UserDTO result = userService.getUserById(userId);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("test@test.com", result.getEmail());
        verify(userRepository).findById(userId);
    }

    @Test
    void getUserById_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        String userId = "999";
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userService.getUserById(userId));
        verify(userRepository).findById(userId);
    }

    @Test
    void updateUser_ShouldUpdateFields_WhenUserExists() {
        // Arrange
        String userId = "123";
        User user = User.builder()
                .id(userId)
                .firstName("Old")
                .lastName("Old")
                .build();

        UserDTO updateDTO = UserDTO.builder()
                .firstName("New")
                .lastName("New")
                .build();

        User updatedUser = User.builder()
                .id(userId)
                .firstName("New")
                .lastName("New")
                .build();

        UserDTO resultDTO = UserDTO.builder()
                .id(userId)
                .firstName("New")
                .lastName("New")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(userMapper.toDTO(any(User.class))).thenReturn(resultDTO);

        // Act
        UserDTO result = userService.updateUser(userId, updateDTO);

        // Assert
        assertNotNull(result);
        assertEquals("New", result.getFirstName());
        assertEquals("New", result.getLastName());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void deleteUser_ShouldDelete_WhenUserExists() {
        // Arrange
        String userId = "123";
        when(userRepository.existsById(userId)).thenReturn(true);

        // Act
        userService.deleteUser(userId);

        // Assert
        verify(userRepository).deleteById(userId);
    }

    @Test
    void deleteUser_ShouldThrowException_WhenUserDoesNotExist() {
        // Arrange
        String userId = "999";
        when(userRepository.existsById(userId)).thenReturn(false);

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(userId));
        verify(userRepository, never()).deleteById(userId);
    }
}
