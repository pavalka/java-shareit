package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {
    Collection<ItemDto> getAllItemsForUser(long userId);
    ItemDto getItemById(long itemId);
    ItemDto createNewItem(long ownerId, ItemDto itemDto);
    ItemDto updateItem(long ownerId, ItemDto itemDto);
    Collection<ItemDto> findItemsByNameAndDescription(String text);
}
