package by.aleksabrakor.user_subscriptions_service.service;

import by.aleksabrakor.user_subscriptions_service.dto.UpdateUserRequest;
import by.aleksabrakor.user_subscriptions_service.dto.UserDto;
import by.aleksabrakor.user_subscriptions_service.exception.NotFoundException;
import by.aleksabrakor.user_subscriptions_service.mapper.UserMapper;
import by.aleksabrakor.user_subscriptions_service.model.User;
import by.aleksabrakor.user_subscriptions_service.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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
    @DisplayName("Сохранение юзера")
    void saveUser() {
        // Arrange
        UserDto userDto = new UserDto();
        userDto.setName("Test User");
        userDto.setEmail("test@test.com");

        User user = new User();
        user.setName("Test User");
        user.setEmail("test@test.com");

        User savedUser = new User();
        savedUser.setName("Test User");
        savedUser.setEmail("test@test.com");
        savedUser.setId(1L);

        UserDto savedUserDto = new UserDto();
        savedUserDto.setName("Test User");
        savedUserDto.setEmail("test@test.com");
        savedUserDto.setId(1L);

        when(userMapper.userDtoToEntity(userDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(savedUser);
        when(userMapper.entityToUserDto(savedUser)).thenReturn(savedUserDto);

        //Act
        UserDto result = userService.saveUser(userDto);

        //Assert
        assertNotNull(result);
        assertEquals(savedUserDto.getName(), result.getName());
        assertEquals(savedUserDto.getEmail(), result.getEmail());
        assertEquals(savedUserDto.getId(), result.getId());
        verify(userMapper, times(1)).userDtoToEntity(userDto);
        verify(userMapper, times(1)).entityToUserDto(savedUser);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("Поиск списка всех юзеров")
    void findAllUsers() {
        // Arrange
        List<User> users = Arrays.asList(new User(), new User());
        List<UserDto> usersDto = Arrays.asList(new UserDto(), new UserDto());

        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.toDtoList(users)).thenReturn(usersDto);

        //Act
        List<UserDto> result = userService.findAllUsers();

        //Assert
        assertNotNull(result);
        assertEquals(usersDto.size(), result.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Поиск юзера по существующему id")
    void findUserById_ShouldReturnUser_WhenUserExists() {
        // Arrange
        Long userId = 1L;

        User user = new User();
        user.setId(userId);
        user.setName("Test User");
        user.setEmail("test@test.com");

        UserDto userDto = new UserDto();
        userDto.setName("Test User");
        userDto.setEmail("test@test.com");
        userDto.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.entityToUserDto(user)).thenReturn(userDto);

        //Act
        UserDto result = userService.findUserById(userId);

        //Assert
        assertNotNull(result);
        assertEquals(userDto.getName(), result.getName());
        assertEquals(userDto.getEmail(), result.getEmail());
        assertEquals(userDto.getId(), result.getId());
        verify(userMapper, times(1)).entityToUserDto(user);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("Выброс NotFoundException при поиске юзера, если id не существует")
    void findUserById_ShouldThrowNotFoundException_WhenUserIdNotExists() {
        // Arrange
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> userService.findUserById(userId));
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("Обновление юзера,  по существующему id")
    void updateUser_ShouldReturnUser_WhenUserIdExists() {
        // Arrange
        Long userId = 1L;
        UpdateUserRequest updateDto = new UpdateUserRequest();
        updateDto.setId(userId);
        updateDto.setName("Updated User");


        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName("Original User");
        existingUser.setEmail("test@test.com");

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setName("Updated User");
        updatedUser.setEmail("test@test.com");


        UserDto updatedUserDto = new UserDto();
        updatedUserDto.setId(userId);
        updatedUserDto.setName("Updated User");
        updatedUserDto.setEmail("test@test.com");


        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(userMapper.entityToUserDto(updatedUser)).thenReturn(updatedUserDto);

        //Act
        UserDto resultUserDto = userService.updateUser(updateDto, userId);

        //Assert
        assertNotNull(resultUserDto);
        assertEquals(updateDto.getName(), resultUserDto.getName());
        assertNotEquals(updateDto.getEmail(), resultUserDto.getEmail());
        assertEquals(userId, resultUserDto.getId());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(existingUser);
        verify(userMapper, times(1)).entityToUserDto(updatedUser);
    }

    @Test
    @DisplayName("Выброс NotFoundException при обновлении юзера, если id не существует")
    void updateUser_ShouldThrowNotFoundException_WhenUserIdNotExists() {
        // Arrange
        Long userId = 999L;
        UpdateUserRequest updateDto = new UpdateUserRequest();
        updateDto.setId(userId);
        updateDto.setName("Updated User");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> userService.updateUser(updateDto, userId));

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any(User.class));
        verify(userMapper, never()).entityToUserDto(any(User.class));
    }

    @Test
    @DisplayName("Удаление юзера,  по существующему id")
    void deleteUser_ShouldDeleteUser_WhenUserIdExists() {
        // Arrange
        Long userId = 1L;

        User existingUser = new User();
        existingUser.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        doNothing().when(userRepository).deleteById(userId);

        //Act
        userService.deleteUser(userId);

        //Assert
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).deleteById(userId);
    }
}