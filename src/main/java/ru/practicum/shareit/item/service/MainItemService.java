package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface MainItemService {
    Collection<ItemDto> getAllItemsForUser(long userId);

    ItemDto createNewItem(long ownerId, ItemDto itemDto);

    ItemDto updateItem(long ownerId, ItemDto itemDto);

    Collection<ItemDto> findItemsByNameAndDescription(String text);

    ItemDto getItemByIdAndUser(long userId, long itemId);

    CommentDto createComment(long userId, long itemId, CommentDto commentDto);
}
