package ru.practicum.shareit.user.repository;

import lombok.NonNull;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.exceptions.UniqueEmailException;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Repository
public class InMemoryUserStorage implements UserDao {
    private static long nextUserId = 1;
    private final Map<Long, User> userStorage = new HashMap<>();
    private final Set<String> setOfEmails = new HashSet<>();

    @Override
    public Optional<User> getUserById(long userId) {
        return Optional.ofNullable(userStorage.get(userId));
    }

    @Override
    public Collection<User> getAllUsers() {
        return userStorage.values();
    }

    @Override
    public User save(@NonNull User user) {
        if (!setOfEmails.add(user.getEmail())) {
            throw new UniqueEmailException(String.format("Пользователь с email = %s уже существует", user.getEmail()));
        }
        user.setId(getNextUserId());
        userStorage.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> updateName(long userId, @NonNull String userName) {
        var wrappedUser = getUserById(userId);

        if (wrappedUser.isPresent()) {
            wrappedUser.get().setName(userName);
            userStorage.put(userId, wrappedUser.get());
        }
        return wrappedUser;
    }

    @Override
    public Optional<User> updateEmail(long userId, @NonNull String userEmail) {
        var wrappedUser = getUserById(userId);

        if (wrappedUser.isPresent()) {
            if (!setOfEmails.add(userEmail)) {
                throw new UniqueEmailException(String.format("Пользователь с email = %s уже существует", userEmail));
            }
            setOfEmails.remove(wrappedUser.get().getEmail());
            wrappedUser.get().setEmail(userEmail);
            userStorage.put(userId, wrappedUser.get());
        }
        return wrappedUser;
    }

    @Override
    public Optional<User> updateNameAndEmail(long userId, @NonNull String userName, @NonNull String userEmail) {
        var wrappedUser = getUserById(userId);

        if (wrappedUser.isPresent()) {
            if (!setOfEmails.add(userEmail)) {
                throw new UniqueEmailException(String.format("Пользователь с email = %s уже существует", userEmail));
            }
            setOfEmails.remove(wrappedUser.get().getEmail());
            wrappedUser.get().setName(userName);
            wrappedUser.get().setEmail(userEmail);
            userStorage.put(userId, wrappedUser.get());
        }
        return wrappedUser;
    }

    @Override
    public void delete(long userId) {
        var wrappedUser = getUserById(userId);

        if (wrappedUser.isPresent()) {
            setOfEmails.remove(wrappedUser.get().getEmail());
            userStorage.remove(userId);
        }
    }

    private long getNextUserId() {
        return nextUserId++;
    }
}
