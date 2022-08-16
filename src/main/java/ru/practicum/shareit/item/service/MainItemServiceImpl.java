package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MainItemServiceImpl implements MainItemService {
    private final UserService userService;
    private final ItemService itemService;

    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public Collection<ItemDto> getAllItemsForUser(long userId) {
        var user = userService.getUserById(userId);
        return ItemMapper.mapItemsCollectionToItemDto(itemService.getAllItemsForUser(user));
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ItemDto createNewItem(long ownerId, ItemDto itemDto) {
        var user = userService.getUserById(ownerId);
        var item = ItemMapper.mapItemDtoToItem(itemDto, user);

        return ItemMapper.mapItemToItemDto(itemService.createNewItem(item));
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ItemDto updateItem(long ownerId, ItemDto itemDto) {
        var user = userService.getUserById(ownerId);
        var item = ItemMapper.mapItemDtoToItem(itemDto, null);

        return ItemMapper.mapItemToItemDto(itemService.updateItem(item, user));
    }

    @Override
    public Collection<ItemDto> findItemsByNameAndDescription(String text) {
        return ItemMapper.mapItemsCollectionToItemDto(itemService.findItemsByNameAndDescription(text));
    }

    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public ItemDto getItemByIdAndUser(long userId, long itemId) {
        var user = userService.getUserById(userId);
        return ItemMapper.mapItemToItemDto(itemService.getItemByIdAndUser(user, itemId));
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public CommentDto createComment(long userId, long itemId, CommentDto commentDto) {
        var user = userService.getUserById(userId);
        var item = itemService.getItemById(itemId);
        var comment = CommentMapper.mapDtoToComment(commentDto, item, user);

        return CommentMapper.mapCommentToDto(itemService.createComment(comment));
    }
}
