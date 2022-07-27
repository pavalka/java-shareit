package ru.practicum.shareit.user.validator;

public class UserValidator {
    public static boolean isUserEmailNotValid(String email) {
        return email == null || email.isBlank() || !email.contains("@") || !email.contains(".") || email.contains(" ")
                || email.startsWith("@") || email.endsWith("@");
    }

    public static boolean isUserNameNotValid(String name) {
        return name == null || name.isBlank();
    }

    private UserValidator() {
    }
}
