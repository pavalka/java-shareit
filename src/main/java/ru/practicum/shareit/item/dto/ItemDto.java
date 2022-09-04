package ru.practicum.shareit.item.dto;

import lombok.Data;
import java.util.Collection;

@Data
public class ItemDto {
    private long id;

    private String name;

    private String description;

    private Boolean available;

    private BookingInfoDto lastBooking;

    private BookingInfoDto nextBooking;

    private Collection<CommentDto> comments;

    private Long requestId;
}
