package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {
    public static ItemDto mapItemToItemDto(Item item) {
        if (item == null) {
            return null;
        }

        var itemDto = new ItemDto();

        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setLastBooking(BookingInfoMapper.mapBookingToBookingInfoDto(item.getLastBooking()));
        itemDto.setNextBooking(BookingInfoMapper.mapBookingToBookingInfoDto(item.getNextBooking()));
        itemDto.setComments(CommentMapper.mapCommentsToDto(item.getComments()));
        return itemDto;
    }

    public static Item mapItemDtoToItem(ItemDto itemDto, User user) {
        if (itemDto == null) {
            return null;
        }

        var item = new Item();

        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(user);
        return item;
    }

    public static Collection<ItemDto> mapItemsCollectionToItemDto(Collection<Item> items) {
        if (items == null) {
            return null;
        }
        return items.stream().map(ItemMapper::mapItemToItemDto).collect(Collectors.toCollection(ArrayList::new));
    }
}
