package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserDao;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class UserServiceImplTest {
    private long nextNum = 1;
    private final UserDao userRepository;
    private final UserServiceImpl userService;

    @AfterEach
    void clearBd() {
        userRepository.deleteAll();
    }

    @Test
    void getUserById() {
        var user = userRepository.save(createUserObj());

        var result = assertDoesNotThrow(() -> userService.getUserById(user.getId()));

        assertEquals(user.getId(), result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
    }

    @Test
    void createUser() {
        var userDto = createUserDtoObj(0);

        var result = userService.createUser(userDto);

        assertNotEquals(0, result.getId());
        assertEquals(userDto.getName(), result.getName());
        assertEquals(userDto.getEmail(), result.getEmail());
    }

    @Test
    void updateUser() {
        var user = userRepository.save(createUserObj());
        var userDto = createUserDtoObj(user.getId());

        userDto.setName("New Name");
        userDto.setEmail("new_email@email.ru");

        assertDoesNotThrow(() -> userService.updateUser(userDto));

        var result = userRepository.findById(user.getId());

        assertTrue(result.isPresent());
        assertEquals(user.getId(), result.get().getId());
        assertEquals(userDto.getName(), result.get().getName());
        assertEquals(userDto.getEmail(), result.get().getEmail());
    }

    @Test
    void deleteUser() {
        var user = userRepository.save(createUserObj());
//        long userId = user.getId();

        assertDoesNotThrow(() -> userService.deleteUser(user.getId()));

        var result = userRepository.findById(user.getId());

        assertTrue(result.isEmpty());
    }

    @Test
    void getAllUsers() {
        var user = userRepository.save(createUserObj());

        var result = userService.getAllUsers().toArray(new UserDto[1]);

        assertEquals(1, result.length);
        assertEquals(user.getId(), result[0].getId());
        assertEquals(user.getName(), result[0].getName());
        assertEquals(user.getEmail(), result[0].getEmail());
    }

    private long getNextNum() {
        return nextNum++;
    }

    private User createUserObj() {
        var user = new User();
        var userId = getNextNum();

        user.setName("user " + userId);
        user.setEmail(String.format("user%d@email.ru", userId));
        return user;
    }

    private UserDto createUserDtoObj(long userId) {
        var userDto = new UserDto();

        userDto.setId(userId);
        userDto.setName("user " + userId);
        userDto.setEmail(String.format("user%d@email.ru", userId));
        return userDto;
    }
}