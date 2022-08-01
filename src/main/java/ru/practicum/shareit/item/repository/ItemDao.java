package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.Item;

import java.util.Collection;
import java.util.Optional;

public interface ItemDao {
    Collection<Item> getAllItemsByOwnerId(long ownerId);

    Optional<Item> getItemById(long itemId);

    Item save(Item item);

    Optional<Item> update(Item item);

    Collection<Item> findAvailableItemsByNameAndDescription(String text);
}
