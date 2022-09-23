package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.repository.UserDao;

import java.util.Collection;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserServiceImpl implements UserService {
    private final UserDao userDao;

    @Override
    public UserDto getUserById(long userId) {
        return UserMapper.mapUserToUserDto(userDao.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("Пользователь с id = %d не найден", userId))));
    }

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        var user = UserMapper.mapUserDtoToUser(userDto);

        return UserMapper.mapUserToUserDto(userDao.save(user));
    }

    @Override
    @Transactional
    public UserDto updateUser(UserDto userDto) {
        User updatedUser = userDao.findById(userDto.getId()).orElseThrow(() -> new UserNotFoundException(
                String.format("Пользователь с id = %d не найден", userDto.getId())));

        if (userDto.getName() != null) {
            updatedUser.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            updatedUser.setEmail(userDto.getEmail());
        }

        return UserMapper.mapUserToUserDto(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(long userId) {
        var user = userDao.findById(userId).orElseThrow(() -> new UserNotFoundException(
                        String.format("Пользователь с id = %d не найден", userId)));
        userDao.delete(user);
    }

    @Override
    public Collection<UserDto> getAllUsers() {
        return UserMapper.mapUserCollectionToUserDto(userDao.findAll());
    }
}
