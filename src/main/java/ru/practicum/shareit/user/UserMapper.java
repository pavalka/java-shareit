package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Component
public class UserMapper {
    public UserDto mapUserToUserDto(User user) {
        if (user == null) {
            return null;
        }

        var userDto = new UserDto();

        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        return userDto;
    }

    public User mapUserDtoToUser(UserDto userDto) {
        if (userDto == null) {
            return null;
        }

        var user = new User();

        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        return user;
    }

    public Collection<UserDto> mapUserCollectionToUserDto(Collection<User> users) {
        if (users == null) {
            return null;
        }
        return users.stream().map(this::mapUserToUserDto).collect(Collectors.toCollection(ArrayList::new));
    }
}
