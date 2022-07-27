package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exceptions.ItemAvailabilityValidationException;
import ru.practicum.shareit.item.exceptions.ItemDescriptionValidationException;
import ru.practicum.shareit.item.exceptions.ItemNameValidationException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.validator.ItemValidator;

import java.util.Collection;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable("itemId") long itemId) {
        return itemService.getItemById(itemId);
    }

    @GetMapping
    public Collection<ItemDto> getItemsByUserId(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getAllItemsForUser(userId);
    }

    @PostMapping
    public ItemDto createNewItem(@RequestHeader("X-Sharer-User-Id") long ownerId, @RequestBody ItemDto itemDto) {
        if (ItemValidator.isItemNameNotValid(itemDto.getName())) {
            throw new ItemNameValidationException("item.name = " + itemDto.getName());
        }
        if (ItemValidator.isItemDescriptionNotValid(itemDto.getDescription())) {
            throw new ItemDescriptionValidationException("item.description = " + itemDto.getDescription());
        }
        if (ItemValidator.isItemAvailabilityNotValid(itemDto.getAvailable())) {
            throw new ItemAvailabilityValidationException("item.available = " + itemDto.getAvailable());
        }
        return itemService.createNewItem(ownerId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") long ownerId, @PathVariable("itemId") long itemId,
                              @RequestBody ItemDto itemDto) {
        if (itemDto.getName() != null && ItemValidator.isItemNameNotValid(itemDto.getName())) {
            throw new ItemNameValidationException("item.name = " + itemDto.getName());
        }
        if (itemDto.getDescription() != null && ItemValidator.isItemDescriptionNotValid(itemDto.getDescription())) {
            throw new ItemDescriptionValidationException("item.description = " + itemDto.getDescription());
        }
        itemDto.setId(itemId);
        return itemService.updateItem(ownerId, itemDto);
    }

    @GetMapping("/search")
    public Collection<ItemDto> findItemsByNameAndDescription(@RequestParam(name = "text") String text) {
        return itemService.findItemsByNameAndDescription(text);
    }
}
