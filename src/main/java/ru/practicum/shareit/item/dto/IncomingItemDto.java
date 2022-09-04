package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.validation.CreateItemValidationGroup;
import ru.practicum.shareit.item.validation.UpdateItemValidationGroup;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Getter
@Setter
public class IncomingItemDto {
    private long id;

    @NotBlank(groups = CreateItemValidationGroup.class)
    @Size(min = 1, groups = UpdateItemValidationGroup.class)
    private String name;

    @NotBlank(groups = CreateItemValidationGroup.class)
    @Size(min = 1, groups = UpdateItemValidationGroup.class)
    private String description;

    @NotNull(groups = CreateItemValidationGroup.class)
    private Boolean available;

    @Positive(groups = {CreateItemValidationGroup.class, UpdateItemValidationGroup.class})
    private Long requestId;
}
