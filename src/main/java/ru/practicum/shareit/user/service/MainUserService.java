package ru.practicum.shareit.user.service;

import lombok.NonNull;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface MainUserService {
    UserDto getUserById(long userId);

    UserDto createUser(@NonNull UserDto userDto);

    UserDto updateUser(@NonNull UserDto userDto);

    void deleteUser(long userId);

    Collection<UserDto> getAllUsers();
}
