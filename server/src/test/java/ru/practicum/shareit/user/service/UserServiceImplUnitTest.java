package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.repository.UserDao;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplUnitTest {
    private long nextId = 1;

    @Mock
    private UserDao userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void getUserByIdThrowsExceptionWhenUserIdIsInvalid() {
        var errMsg = "Пользователь с id = 10 не найден";

        Mockito.when(userRepository.findById(10L))
                .thenReturn(Optional.empty());

        var ex = assertThrows(UserNotFoundException.class, () -> userService.getUserById(10));

        assertEquals(errMsg, ex.getMessage());
    }

    @Test
    void getUserByIdReturnUserDto() {
        var user = createUserObj();

        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        var result = assertDoesNotThrow(() -> userService.getUserById(1));

        assertEquals(user.getId(), result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
    }

    @Test
    void createUserReturnNEwUser() {
        var userDto = createUserDtoObj();

        userDto.setId(0);

        Mockito.when(userRepository.save(Mockito.any(User.class)))
                .thenAnswer(invocation -> {
                    var user = invocation.<User>getArgument(0);
                    user.setId(getNextId());
                    return user;
                });

        var result = userService.createUser(userDto);

        assertNotEquals(0, result.getId());
        assertEquals(userDto.getName(), result.getName());
        assertEquals(userDto.getEmail(), result.getEmail());
    }

    @Test
    void updateUserThrowsExceptionWhenUsrIdIsInvalid() {
        var errMsg = "Пользователь с id = 10 не найден";
        var userDto = createUserDtoObj();

        userDto.setId(10);
        Mockito.when(userRepository.findById(10L))
                .thenReturn(Optional.empty());

        var ex = assertThrows(UserNotFoundException.class, () -> userService.updateUser(userDto));

        assertEquals(errMsg, ex.getMessage());
    }

    @Test
    void updateUserUpdateUserNameAndReturnUserDto() {
        var newName = "NewName";
        var userDto = createUserDtoObj();
        var user = createUserObj();

        userDto.setId(1);
        userDto.setName(newName);
        userDto.setEmail(null);
        user.setId(1L);
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        var result = assertDoesNotThrow(() -> userService.updateUser(userDto));

        assertEquals(userDto.getId(), result.getId());
        assertEquals(newName, result.getName());
        assertEquals(user.getEmail(), result.getEmail());
    }

    @Test
    void updateUserUpdateUserEmailAndReturnUserDto() {
        var newEmail = "new_email@email.ru";
        var userDto = createUserDtoObj();
        var user = createUserObj();

        userDto.setId(1);
        userDto.setName(null);
        userDto.setEmail(newEmail);
        user.setId(1L);
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        var result = assertDoesNotThrow(() -> userService.updateUser(userDto));

        assertEquals(userDto.getId(), result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(newEmail, result.getEmail());
    }

    @Test
    void deleteUserThrowsExceptionWhenUserIdIsInvalid() {
        var errMsg = "Пользователь с id = 10 не найден";

        Mockito.when(userRepository.findById(10L))
                .thenReturn(Optional.empty());

        var ex = assertThrows(UserNotFoundException.class, () -> userService.deleteUser(10));

        assertEquals(errMsg, ex.getMessage());
        Mockito.verify(userRepository, Mockito.never()).delete(Mockito.any(User.class));
    }

    @Test
    void deleteUserDeleteUser() {
        var user = createUserObj();

        Mockito.when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        assertDoesNotThrow(() -> userService.deleteUser(user.getId()));
        Mockito.verify(userRepository, Mockito.times(1)).delete(user);
    }

    @Test
    void getAllUsersReturnEmptyCollectionWhenNoUserFound() {
        Mockito.when(userRepository.findAll())
                .thenReturn(List.of());

        var result = assertDoesNotThrow(() -> userService.getAllUsers());

        assertTrue(result.isEmpty());
    }

    @Test
    void getAllUsersReturnCollection() {
        var user = createUserObj();

        Mockito.when(userRepository.findAll())
                .thenReturn(List.of(user));

        var result = assertDoesNotThrow(() -> userService.getAllUsers().toArray(new UserDto[1]));

        assertEquals(1, result.length);
        assertEquals(user.getId(), result[0].getId());
        assertEquals(user.getName(), result[0].getName());
        assertEquals(user.getEmail(), result[0].getEmail());
    }

    private long getNextId() {
        return nextId++;
    }

    private User createUserObj() {
        var user = new User();
        var userId = getNextId();

        user.setId(userId);
        user.setName("user " + userId);
        user.setEmail(String.format("user%d@email.ru", userId));
        return user;
    }

    private UserDto createUserDtoObj() {
        var userDto = new UserDto();
        var userId = getNextId();

        userDto.setId(userId);
        userDto.setName("user " + userId);
        userDto.setEmail(String.format("user%d@email.ru", userId));
        return userDto;
    }
}