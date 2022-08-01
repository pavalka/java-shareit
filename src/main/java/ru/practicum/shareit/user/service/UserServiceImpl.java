package ru.practicum.shareit.user.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.repository.UserDao;

import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserServiceImpl implements UserService {
    private final UserDao userDao;

    @Override
    public UserDto getUserById(long userId) {
        return UserMapper.mapUserToUserDto(
                userDao.getUserById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("Пользователь с id = %d не найден", userId)))
        );
    }

    @Override
    public UserDto createUser(@NonNull UserDto userDto) {
        var user = userDao.save(UserMapper.mapUserDtoToUser(userDto));

        return UserMapper.mapUserToUserDto(user);
    }

    @Override
    public UserDto updateUser(@NonNull UserDto userDto) {
        Optional<User> wrappedUser = Optional.empty();

        if (userDto.getName() != null && userDto.getEmail() != null) {
            wrappedUser = userDao.updateNameAndEmail(userDto.getId(), userDto.getName(), userDto.getEmail());
        } else if (userDto.getName() != null) {
            wrappedUser = userDao.updateName(userDto.getId(), userDto.getName());
        } else if (userDto.getEmail() != null) {
            wrappedUser = userDao.updateEmail(userDto.getId(), userDto.getEmail());
        }
        return UserMapper.mapUserToUserDto(wrappedUser.orElseThrow(() -> new UserNotFoundException(
                String.format("Пользователь с id = %d не найден", userDto.getId()))));
    }

    @Override
    public void deleteUser(long userId) {
        userDao.delete(userId);
    }

    @Override
    public Collection<UserDto> getAllUsers() {
        return UserMapper.mapUserCollectionToUserDto(userDao.getAllUsers());
    }
}
