package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;

import java.util.Collection;

public interface ItemService {
    Collection<Item> getAllItemsForUser(long userId);

    Item getItemById(long itemId);

    Item createNewItem(long ownerId, Item item);

    Item updateItem(long ownerId, Item item);

    Collection<Item> findItemsByNameAndDescription(String text);

    Item getItemById(long userId, long itemId);

    Comment createComment(long userId, long itemId, String text);
}
