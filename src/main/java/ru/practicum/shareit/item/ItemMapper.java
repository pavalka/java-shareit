package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Component
public class ItemMapper {
    public ItemDto mapItemToItemDto(Item item) {
        if (item == null) {
            return null;
        }

        var itemDto = new ItemDto();

        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.isAvailable());
        return itemDto;
    }

    public Item mapItemDtoToItem(ItemDto itemDto) {
        if (itemDto == null) {
            return null;
        }

        var item = new Item();

        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        return item;
    }

    public Collection<ItemDto> mapItemsCollectionToItemDto(Collection<Item> items) {
        if (items == null) {
            return null;
        }

        return items.stream().map(this::mapItemToItemDto).collect(Collectors.toCollection(ArrayList::new));
    }
}
