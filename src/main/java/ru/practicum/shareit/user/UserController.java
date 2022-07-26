package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exceptions.UserEmailValidationException;
import ru.practicum.shareit.user.exceptions.UserNameValidationException;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.validator.UserValidator;

import java.util.Collection;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserController {
    private final UserService userService;

    @GetMapping
    public Collection<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable("userId") long userId) {
        return userService.getUserById(userId);
    }

    @PostMapping
    public UserDto createNewUser(@RequestBody UserDto userDto) {
        if (UserValidator.isUserNameNotValid(userDto.getName())) {
            throw new UserNameValidationException(userDto.getName());
        }
        if (UserValidator.isUserEmailNotValid(userDto.getEmail())) {
            throw new UserEmailValidationException(userDto.getEmail());
        }
        return userService.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable("userId") long userId, @RequestBody UserDto userDto) {
        if (userDto.getName() != null && UserValidator.isUserNameNotValid(userDto.getName())) {
            throw new UserNameValidationException(userDto.getName());
        }
        if (userDto.getEmail() != null && UserValidator.isUserEmailNotValid(userDto.getEmail())) {
            throw new UserEmailValidationException(userDto.getEmail());
        }
        userDto.setId(userId);
        return userService.updateUser(userDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable("userId") long userId) {
        userService.deleteUser(userId);
    }
}
