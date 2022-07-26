package ru.practicum.shareit.user.service;

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
    private final UserMapper mapper;

    @Override
    public UserDto getUserById(long userId) {
        return mapper.mapUserToUserDto(
                userDao.getUserById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("Пользователь с id = %d не найден", userId)))
        );
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        if (userDto == null) {
            throw new NullPointerException("userDto = null");
        }

        var user = userDao.save(mapper.mapUserDtoToUser(userDto));

        return mapper.mapUserToUserDto(user);
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        if (userDto == null) {
            throw new NullPointerException("userDto = null");
        }

        Optional<User> wrappedUser = Optional.empty();

        if (userDto.getName() != null && userDto.getEmail() != null) {
            wrappedUser = userDao.updateNameAndEmail(userDto.getId(), userDto.getName(), userDto.getEmail());
        } else if (userDto.getName() != null) {
            wrappedUser = userDao.updateName(userDto.getId(), userDto.getName());
        } else if (userDto.getEmail() != null) {
            wrappedUser = userDao.updateEmail(userDto.getId(), userDto.getEmail());
        }
        return mapper.mapUserToUserDto(wrappedUser.orElseThrow(() -> new UserNotFoundException(
                String.format("Пользователь с id = %d не найден", userDto.getId()))));
    }

    @Override
    public void deleteUser(long userId) {
        userDao.delete(userId);
    }

    @Override
    public Collection<UserDto> getAllUsers() {
        return mapper.mapUserCollectionToUserDto(userDao.getAllUsers());
    }
}
