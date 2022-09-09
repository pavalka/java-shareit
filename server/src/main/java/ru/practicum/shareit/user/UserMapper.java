package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {
    public static UserDto mapUserToUserDto(User user) {
        if (user == null) {
            return null;
        }

        var userDto = new UserDto();

        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        return userDto;
    }

    public static User mapUserDtoToUser(UserDto userDto) {
        if (userDto == null) {
            return null;
        }

        var user = new User();

        user.setId(userDto.getId());
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        return user;
    }

    public static Collection<UserDto> mapUserCollectionToUserDto(Collection<User> users) {
        if (users == null) {
            return null;
        }

        return users.stream().map(UserMapper::mapUserToUserDto).collect(Collectors.toCollection(ArrayList::new));
    }
}
