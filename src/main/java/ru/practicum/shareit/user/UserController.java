package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.validation.CreateUserValidationGroup;
import ru.practicum.shareit.user.validation.UpdateUserValidationGroup;

import java.util.Collection;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserController {
    private final UserService userService;

    @GetMapping
    public Collection<UserDto> getAllUsers() {
        return UserMapper.mapUserCollectionToUserDto(userService.getAllUsers());
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable("userId") long userId) {
        return UserMapper.mapUserToUserDto(userService.getUserById(userId));
    }

    @PostMapping
    public UserDto createNewUser(@Validated(CreateUserValidationGroup.class) @RequestBody UserDto userDto) {
        return UserMapper.mapUserToUserDto(userService.createUser(UserMapper.mapUserDtoToUser(userDto)));
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable("userId") long userId,
                              @Validated(UpdateUserValidationGroup.class) @RequestBody UserDto userDto) {
        userDto.setId(userId);
        return UserMapper.mapUserToUserDto(userService.updateUser(UserMapper.mapUserDtoToUser(userDto)));
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable("userId") long userId) {
        userService.deleteUser(userId);
    }
}
