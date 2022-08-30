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

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Validated
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable("itemId") long itemId) {
        return itemService.getItemByIdAndUser(userId, itemId);
    }

    @GetMapping
    public Collection<ItemDto> getItemsByUserId(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero long from,
            @RequestParam(name = "size", defaultValue = "10") @Positive int size) {
        return itemService.getAllItemsForUser(userId, from, size);
    }

    @PostMapping
    public ItemDto createNewItem(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                 @Validated(CreateItemValidationGroup.class) @RequestBody ItemDto itemDto) {
        return itemService.createNewItem(ownerId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") long ownerId, @PathVariable("itemId") long itemId,
                              @Validated(UpdateItemValidationGroup.class) @RequestBody ItemDto itemDto) {
        itemDto.setId(itemId);
        return itemService.updateItem(itemDto, ownerId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> findItemsByNameAndDescription(
            @RequestParam(name = "text") String text,
            @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero long from,
            @RequestParam(name = "size", defaultValue = "10") @Positive int size) {
        return itemService.findItemsByNameAndDescription(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable("itemId") long itemId,
                                 @Valid @RequestBody CommentDto commentDto) {
        return itemService.createComment(userId, itemId, commentDto);
    }
}
