package ru.practicum.shareit.user.dto;

import lombok.Data;
import ru.practicum.shareit.user.validation.CreateUserValidationGroup;
import ru.practicum.shareit.user.validation.UpdateUserValidationGroup;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class UserDto {
    private long id;

    @NotBlank(groups = CreateUserValidationGroup.class)
    @Size(min = 1, groups = UpdateUserValidationGroup.class)
    private String name;

    @NotBlank(groups = CreateUserValidationGroup.class)
    @Email(groups = {CreateUserValidationGroup.class, UpdateUserValidationGroup.class})
    private String email;
}
