package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MainUserServiceImpl implements MainUserService {
    private final UserService userService;

    @Override
    public UserDto getUserById(long userId) {
        return UserMapper.mapUserToUserDto(userService.getUserById(userId));
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        var user = UserMapper.mapUserDtoToUser(userDto);

        return UserMapper.mapUserToUserDto(userService.createUser(user));
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        var user = UserMapper.mapUserDtoToUser(userDto);

        return UserMapper.mapUserToUserDto(userService.updateUser(user));
    }

    @Override
    public void deleteUser(long userId) {
        userService.deleteUser(userId);
    }

    @Override
    public Collection<UserDto> getAllUsers() {
        return UserMapper.mapUserCollectionToUserDto(userService.getAllUsers());
    }
}
