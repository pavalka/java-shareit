package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.IncomingItemDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {
    Collection<ItemDto> getAllItemsForUser(long userId, long from, int size);

    ItemDto createNewItem(long ownerId, IncomingItemDto itemDto);

    ItemDto updateItem(IncomingItemDto itemDto, long userId);

    Collection<ItemDto> findItemsByNameAndDescription(String text, long from, int size);

    ItemDto getItemByIdAndUser(long userId, long itemId);

    CommentDto createComment(long userId, long itemId, CommentDto commentDto);
}
