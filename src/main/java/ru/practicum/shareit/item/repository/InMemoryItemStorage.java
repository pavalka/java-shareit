package ru.practicum.shareit.item.repository;

import lombok.NonNull;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.Item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class InMemoryItemStorage implements ItemDao {
    private static long nextItemId = 1;
    private final Map<Long, Item> itemStorage = new HashMap<>();

    @Override
    public Collection<Item> getAllItemsByOwnerId(long ownerId) {
        return itemStorage.values().stream().filter(item -> item.getOwnerId() == ownerId)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public Optional<Item> getItemById(long itemId) {
        return Optional.ofNullable(itemStorage.get(itemId));
    }

    @Override
    public Item save(@NonNull Item item) {
        item.setId(getNextItemId());
        itemStorage.put(item.getId(), item);
        return item;
    }

    @Override
    public Optional<Item> update(@NonNull Item item) {
        var wrappedItem = getItemById(item.getId());

        if (wrappedItem.isPresent()) {
            itemStorage.put(item.getId(), item);
            wrappedItem = Optional.of(item);
        }
        return wrappedItem;
    }

    @Override
    public Collection<Item> findAvailableItemsByNameAndDescription(String text) {
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }
        return itemStorage.values().stream().filter(
                item -> (item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                        && item.isAvailable())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private long getNextItemId() {
        return nextItemId++;
    }
}
