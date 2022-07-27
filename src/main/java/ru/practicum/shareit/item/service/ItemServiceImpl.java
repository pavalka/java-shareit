package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.exceptions.UserIsNotItemOwnerException;
import ru.practicum.shareit.item.repository.ItemDao;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ItemServiceImpl implements ItemService {
    private final ItemDao itemDao;
    private final UserService userService;
    private final ItemMapper mapper;

    @Override
    public Collection<ItemDto> getAllItemsForUser(long userId) {
        userService.getUserById(userId);
        return mapper.mapItemsCollectionToItemDto(itemDao.getAllItemsByOwnerId(userId));
    }

    @Override
    public ItemDto getItemById(long itemId) {
        return mapper.mapItemToItemDto(itemDao.getItemById(itemId).orElseThrow(
                () -> new ItemNotFoundException(String.format("Элемент с id = %d не найден", itemId))
        ));
    }

    @Override
    public ItemDto createNewItem(long ownerId, ItemDto itemDto) {
        if (itemDto == null) {
            throw new NullPointerException("itemDto = null");
        }
        userService.getUserById(ownerId);

        var item = mapper.mapItemDtoToItem(itemDto);

        item.setOwnerId(ownerId);
        return mapper.mapItemToItemDto(itemDao.save(item));
    }

    @Override
    public ItemDto updateItem(long ownerId, ItemDto itemDto) {
        userService.getUserById(ownerId);
        if (itemDto == null) {
            throw new NullPointerException("itemDto = null");
        }

        var item = itemDao.getItemById(itemDto.getId()).orElseThrow(
                () -> new ItemNotFoundException(String.format("Элемент с id = %d не найден", itemDto.getId())));

        if (item.getOwnerId() != ownerId) {
            throw new UserIsNotItemOwnerException(String.format("Пользователь с id = %d не владелец элемента с id = %d",
                    ownerId, itemDto.getId()));
        }

        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        return mapper.mapItemToItemDto(itemDao.update(item).get());
    }

    @Override
    public Collection<ItemDto> findItemsByNameAndDescription(String text) {
        var foundItems = itemDao.findAvailableItemsByNameAndDescription(text);

        return mapper.mapItemsCollectionToItemDto(foundItems);
    }
}
