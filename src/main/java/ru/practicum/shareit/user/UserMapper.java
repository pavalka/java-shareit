package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {
    public static UserDto mapUserToUserDto(@NonNull User user) {
        var userDto = new UserDto();

        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        return userDto;
    }

    public static User mapUserDtoToUser(@NonNull UserDto userDto) {
        var user = new User();

        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        return user;
    }

    public static Collection<UserDto> mapUserCollectionToUserDto(@NonNull Collection<User> users) {
        return users.stream().map(UserMapper::mapUserToUserDto).collect(Collectors.toCollection(ArrayList::new));
    }
}
