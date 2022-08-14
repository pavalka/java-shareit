package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.User;

import java.util.Collection;

public interface UserService {
    User getUserById(long userId);

    User createUser(User user);

    User updateUser(User user);

    void deleteUser(long userId);

    Collection<User> getAllUsers();
}
