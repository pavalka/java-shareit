package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.User;

import java.util.Collection;
import java.util.Optional;

public interface UserDao {
    Optional<User> getUserById(long userId);

    Collection<User> getAllUsers();

    User save(User user);

    Optional<User> updateName(long userId, String userName);

    Optional<User> updateEmail(long userId, String userEmail);

    Optional<User> updateNameAndEmail(long userId, String userName, String userEmail);

    void delete(long userId);
}
