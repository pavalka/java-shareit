package ru.practicum.shareit.requests.exceptions;

public class RequestNotFoundException extends RuntimeException {
    public RequestNotFoundException(String msg) {
        super(msg);
    }
}
