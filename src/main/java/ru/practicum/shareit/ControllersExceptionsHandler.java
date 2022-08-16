package ru.practicum.shareit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.exceptions.BookingNotAvailableItemException;
import ru.practicum.shareit.booking.exceptions.BookingNotFoundException;
import ru.practicum.shareit.booking.exceptions.BookingTimeConflictsException;
import ru.practicum.shareit.booking.exceptions.IllegalBookingApproveException;
import ru.practicum.shareit.booking.exceptions.ItemBookedByItsOwnerException;
import ru.practicum.shareit.item.exceptions.BookingToCreateCommentNotFoundException;
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.exceptions.UserIsNotItemOwnerException;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;

@RestControllerAdvice
@Slf4j
public class ControllersExceptionsHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorMessage handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex,
                                                                  WebRequest webRequest) {
        String errorMsg;

        if (ex.getParameter().getParameterType() == BookingState.class) {
            errorMsg = "Unknown state: " + webRequest.getParameter("state");
        } else {
            errorMsg = "Parameter " + ex.getName() + "should has the type " + ex.getParameter().getParameterType()
                    .getName();
        }
        logWarn(ex);
        return new ErrorMessage(errorMsg);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({UserNotFoundException.class, ItemNotFoundException.class, UserIsNotItemOwnerException.class,
                       ItemBookedByItsOwnerException.class, BookingNotFoundException.class})
    public ErrorMessage handleNotFoundExceptions(RuntimeException ex) {
        logWarn(ex);
        return new ErrorMessage(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ErrorMessage handleValidationException(MethodArgumentNotValidException ex) {
        logWarn(ex);
        return new ErrorMessage("Field " + ex.getFieldError().getField() + "has an invalid value");
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler({DataIntegrityViolationException.class})
    public ErrorMessage handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        logWarn(ex);
        return new ErrorMessage("data is not correct");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({BookingToCreateCommentNotFoundException.class, IllegalBookingApproveException.class,
                       BookingTimeConflictsException.class, BookingNotAvailableItemException.class})
    public ErrorMessage handleOtherBadRequestExceptions(RuntimeException ex) {
        logWarn(ex);
        return new ErrorMessage(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MissingRequestHeaderException.class, MissingPathVariableException.class})
    public ErrorMessage handleUnavailableRequestData(MissingRequestValueException ex) {
        logWarn(ex);
        return new ErrorMessage("bad request");
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorMessage handleOtherExceptions(Throwable ex) {
        logError(ex);
        return new ErrorMessage("Internal server error");
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
