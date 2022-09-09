package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {
    private long nextId = 1;

    @Test
    void mapUserToUserDtoReturnNullIfArgumentIsNull() {
        assertNull(UserMapper.mapUserToUserDto(null));
    }

    @Test
    void mapUserToUserDtoReturnUserDto() {
        var user = createUser();

        var userDto = UserMapper.mapUserToUserDto(user);

        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
    }

    @Test
    void mapUserDtoToUserReturnNullWhenArgumentIsNull() {
        assertNull(UserMapper.mapUserDtoToUser(null));
    }

    @Test
    void mapUserDtoToUserReturnUser() {
        var userDto = createUserDto();

        var user = UserMapper.mapUserDtoToUser(userDto);

        assertEquals(userDto.getId(), user.getId());
        assertEquals(userDto.getName(), user.getName());
        assertEquals(userDto.getEmail(), user.getEmail());
    }

    @Test
    void mapUserCollectionToUserDtoReturnNullWhenArgumentIsNull() {
        assertNull(UserMapper.mapUserCollectionToUserDto(null));
    }

    @Test
    void mapUserCollectionToUserDtoReturnUserDtoCollection() {
        var user = createUser();

        var userDto = UserMapper.mapUserCollectionToUserDto(List.of(user)).toArray(new UserDto[1]);

        assertEquals(1, userDto.length);
        assertEquals(user.getId(), userDto[0].getId());
        assertEquals(user.getName(), userDto[0].getName());
        assertEquals(user.getEmail(), userDto[0].getEmail());
    }

    private long getNextId() {
        return nextId++;
    }

    private User createUser() {
        var user = new User();
        var userId = getNextId();

        user.setId(userId);
        user.setName("user " + userId);
        user.setEmail(String.format("user%d@email.ru", userId));
        return user;
    }

    private UserDto createUserDto() {
        var userDto = new UserDto();
        var userId = getNextId();

        userDto.setId(userId);
        userDto.setName("user " + userId);
        userDto.setEmail(String.format("user%d@email.ru", userId));
        return userDto;
    }
}