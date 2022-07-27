package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.exceptions.UniqueEmailException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class InMemoryUserStorage implements UserDao {
    private static long nextUserId = 1;
    private final Map<Long, User> userStorage = new HashMap<>();

    @Override
    public Optional<User> getUserById(long userId) {
        return Optional.ofNullable(userStorage.get(userId));
    }

    @Override
    public Collection<User> getAllUsers() {
        return userStorage.values();
    }

    @Override
    public User save(User user) {
        if (isEmailNotUnique(user.getEmail())) {
            throw new UniqueEmailException(String.format("Пользователь с email = %s уже существует", user.getEmail()));
        }
        user.setId(getNextUserId());
        userStorage.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> updateName(long userId, String userName) {
        var wrappedUser = getUserById(userId);

        if (wrappedUser.isPresent()) {
            wrappedUser.get().setName(userName);
            userStorage.put(userId, wrappedUser.get());
        }
        return wrappedUser;
    }

    @Override
    public Optional<User> updateEmail(long userId, String userEmail) {
        var wrappedUser = getUserById(userId);

        if (wrappedUser.isPresent()) {
            if (isEmailNotUnique(userEmail)) {
                throw new UniqueEmailException(String.format("Пользователь с email = %s уже существует", userEmail));
            }
            wrappedUser.get().setEmail(userEmail);
            userStorage.put(userId, wrappedUser.get());
        }
        return wrappedUser;
    }

    @Override
    public Optional<User> updateNameAndEmail(long userId, String userName, String userEmail) {
        var wrappedUser = getUserById(userId);

        if (wrappedUser.isPresent()) {
            if (isEmailNotUnique(userEmail)) {
                throw new UniqueEmailException(String.format("Пользователь с email = %s уже существует", userEmail));
            }
            wrappedUser.get().setName(userName);
            wrappedUser.get().setEmail(userEmail);
            userStorage.put(userId, wrappedUser.get());
        }
        return wrappedUser;
    }

    @Override
    public Optional<User> update(User user) {
        if (user == null) {
            throw new NullPointerException("user = null");
        }

        var wrappedUser = getUserById(user.getId());

        if (wrappedUser.isPresent()) {
            userStorage.put(user.getId(), user);
            wrappedUser = Optional.of(user);
        }
        return wrappedUser;
    }

    @Override
    public void delete(long userId) {
        userStorage.remove(userId);
    }

    private boolean isEmailNotUnique(String email) {
        return userStorage.values().stream().anyMatch(user -> user.getEmail().equals(email));
    }

    private long getNextUserId() {
        return nextUserId++;
    }
}
