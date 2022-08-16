package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.util.Collection;

public interface ItemService {
    Collection<Item> getAllItemsForUser(User user);

    Item getItemById(long itemId);

    Item createNewItem(Item item);

    Item updateItem(Item item, User user);

    Collection<Item> findItemsByNameAndDescription(String text);

    Item getItemByIdAndUser(User user, long itemId);

    Comment createComment(Comment comment);
}
