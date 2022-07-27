package ru.practicum.shareit.item.validator;

public class ItemValidator {
    public static boolean isItemNameNotValid(String name) {
        return name == null || name.isBlank();
    }

    public static boolean isItemDescriptionNotValid(String description) {
        return description == null || description.isBlank();
    }

    public static boolean isItemAvailabilityNotValid(Boolean available) {
        return available == null;
    }

    private ItemValidator() {
    }
}
