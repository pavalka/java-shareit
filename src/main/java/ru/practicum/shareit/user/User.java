package ru.practicum.shareit.user;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {
    private long id;
    private String name;

    @EqualsAndHashCode.Include
    private String email;
}
