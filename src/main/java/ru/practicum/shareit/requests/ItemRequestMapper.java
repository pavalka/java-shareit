package ru.practicum.shareit.requests;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.requests.dto.ItemDtoForRequest;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemRequestMapper {
    public static ItemRequestDto mapToItemRequestDto(ItemRequest request) {
        if (request == null) {
            return null;
        }

        var requestDto = new ItemRequestDto();

        requestDto.setId(request.getId());
        requestDto.setDescription(request.getDescription());
        requestDto.setCreated(request.getCreated());
        requestDto.setItems(mapToItemDtoCollection(request.getItems()));
        return requestDto;
    }

    public static ItemRequest mapToItemRequest(ItemRequestDto requestDto, User author) {
        if (requestDto == null) {
            return null;
        }

        var request = new ItemRequest();

        request.setDescription(requestDto.getDescription());
        request.setAuthor(author);
        return request;
    }

    public static Collection<ItemRequestDto> mapToItemRequestDtoCollection(Collection<ItemRequest> requests) {
        if (requests == null) {
            return null;
        }

        return requests.stream().map(ItemRequestMapper::mapToItemRequestDto).collect(Collectors.toList());
    }

    private static ItemDtoForRequest mapToItemDto(Item item) {
        if (item == null) {
            return null;
        }

        var itemDto = new ItemDtoForRequest();

        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setRequestId(item.getRequest().getId());
        itemDto.setOwnerId(item.getOwner().getId());
        return itemDto;
    }

    private static List<ItemDtoForRequest> mapToItemDtoCollection(Collection<Item> items) {
        if (items == null) {
            return null;
        }
        return items.stream().map(ItemRequestMapper::mapToItemDto).collect(Collectors.toList());
    }
}
