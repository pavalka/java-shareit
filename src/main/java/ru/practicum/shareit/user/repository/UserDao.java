package ru.practicum.shareit.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.User;


public interface UserDao extends JpaRepository<User, Long> {
}
