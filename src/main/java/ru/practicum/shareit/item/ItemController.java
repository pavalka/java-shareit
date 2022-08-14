package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.validation.CreateItemValidationGroup;
import ru.practicum.shareit.item.validation.UpdateItemValidationGroup;

import java.util.Collection;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable("itemId") long itemId) {
        return ItemMapper.mapItemToItemDto(itemService.getItemById(userId, itemId));
    }

    @GetMapping
    public Collection<ItemDto> getItemsByUserId(@RequestHeader("X-Sharer-User-Id") long userId) {
        return ItemMapper.mapItemsCollectionToItemDto(itemService.getAllItemsForUser(userId));
    }

    @PostMapping
    public ItemDto createNewItem(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                 @Validated(CreateItemValidationGroup.class) @RequestBody ItemDto itemDto) {
        return ItemMapper.mapItemToItemDto(itemService.createNewItem(ownerId, ItemMapper.mapItemDtoToItem(itemDto)));
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") long ownerId, @PathVariable("itemId") long itemId,
                              @Validated(UpdateItemValidationGroup.class) @RequestBody ItemDto itemDto) {
        itemDto.setId(itemId);
        return ItemMapper.mapItemToItemDto(itemService.updateItem(ownerId, ItemMapper.mapItemDtoToItem(itemDto)));
    }

    @GetMapping("/search")
    public Collection<ItemDto> findItemsByNameAndDescription(@RequestParam(name = "text") String text) {
        return ItemMapper.mapItemsCollectionToItemDto(itemService.findItemsByNameAndDescription(text));
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable("itemId") long itemId,
                                 @Validated @RequestBody CommentDto commentDto) {
        return CommentMapper.mapCommentToDto(itemService.createComment(userId, itemId, commentDto.getText()));
    }
}
