package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.repository.UserDao;

import java.util.Collection;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserServiceImpl implements UserService {
    private final UserDao userDao;

    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public User getUserById(long userId) {
        return userDao.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("Пользователь с id = %d не найден", userId)));
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public User createUser(User user) {
        return userDao.save(user);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public User updateUser(User user) {
        User updatedUser = userDao.findById(user.getId()).orElseThrow(() -> new UserNotFoundException(
                String.format("Пользователь с id = %d не найден", user.getId())));

        if (user.getName() != null) {
            updatedUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            updatedUser.setEmail(user.getEmail());
        }

        return updatedUser;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deleteUser(long userId) {
        var user = userDao.findById(userId).orElseThrow(() -> new UserNotFoundException(
                        String.format("Пользователь с id = %d не найден", userId)));
        userDao.delete(user);
    }

    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public Collection<User> getAllUsers() {
        return userDao.findAll();
    }
}
