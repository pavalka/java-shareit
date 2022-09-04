package ru.practicum.shareit.requests.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemDtoForRequest {
    private long id;

    private String name;

    private String description;

    private Boolean available;

    private long requestId;

    private long ownerId;
}
