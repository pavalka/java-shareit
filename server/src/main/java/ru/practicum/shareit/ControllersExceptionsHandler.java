package ru.practicum.shareit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.booking.exceptions.BookingNotAvailableItemException;
import ru.practicum.shareit.booking.exceptions.BookingNotFoundException;
import ru.practicum.shareit.booking.exceptions.BookingTimeConflictsException;
import ru.practicum.shareit.booking.exceptions.IllegalBookingApproveException;
import ru.practicum.shareit.booking.exceptions.ItemBookedByItsOwnerException;
import ru.practicum.shareit.item.exceptions.BookingToCreateCommentNotFoundException;
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.exceptions.UserIsNotItemOwnerException;
import ru.practicum.shareit.requests.exceptions.RequestNotFoundException;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;

import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ControllersExceptionsHandler {
    @ExceptionHandler({UserNotFoundException.class, ItemNotFoundException.class, UserIsNotItemOwnerException.class,
                       ItemBookedByItsOwnerException.class, BookingNotFoundException.class,
                       RequestNotFoundException.class})
    public ResponseEntity<Map<String, String>> handleNotFoundExceptions(RuntimeException ex) {
        logWarn(ex);
        return new ResponseEntity<>(Map.of("error", ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({DataIntegrityViolationException.class})
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        logWarn(ex);
        return new ResponseEntity<>(Map.of("error", "data is not correct"), HttpStatus.CONFLICT);
    }

    @ExceptionHandler({BookingToCreateCommentNotFoundException.class, IllegalBookingApproveException.class,
                       BookingTimeConflictsException.class, BookingNotAvailableItemException.class})
    public ResponseEntity<Map<String, String>> handleOtherBadRequestExceptions(RuntimeException ex) {
        logWarn(ex);
        return new ResponseEntity<>(Map.of("error", ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleOtherExceptions(Throwable ex) {
        logError(ex);
        return new ResponseEntity<>(Map.of("error", "Internal server error"), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void logWarn(Throwable ex) {
        StackTraceElement[] stackTrace = ex.getStackTrace();
        log.warn("{}::{}.{} : {}", ex.getClass().getName(), stackTrace[0].getClassName(),
                stackTrace[0].getMethodName(), ex.getMessage());
    }

    private void logError(Throwable ex) {
        StackTraceElement[] stackTrace = ex.getStackTrace();
        log.error("{}::{}.{} : {}", ex.getClass().getName(), stackTrace[0].getClassName(),
                stackTrace[0].getMethodName(), ex.getMessage());
    }
}
