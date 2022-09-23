package ru.practicum.shareit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.shareit.booking.BookingState;

import javax.validation.ConstraintViolationException;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ControllersExceptionsHandler {
    @ExceptionHandler
    public ResponseEntity<Map<String, String>>
    handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex, WebRequest webRequest) {
        String errorMsg;

        if (ex.getParameter().getParameterType() == BookingState.class) {
            errorMsg = "Unknown state: " + webRequest.getParameter("state");
        } else {
            errorMsg = "Parameter " + ex.getName() + "should has the type " + ex.getParameter().getParameterType()
                    .getName();
        }
        logWarn(ex);
        return new ResponseEntity<>(Map.of("error", errorMsg), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex) {
        logWarn(ex);
        return new ResponseEntity<>(Map.of("error", "Field " + ex.getFieldError().getField() + " has an invalid value"),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<Map<String, String>> handleConstraintViolationException(ConstraintViolationException ex) {
        logWarn(ex);
        return new ResponseEntity<>(Map.of("error", ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({MissingRequestHeaderException.class, MissingPathVariableException.class})
    public ResponseEntity<Map<String, String>> handleUnavailableRequestData(MissingRequestValueException ex) {
        logWarn(ex);
        return new ResponseEntity<>(Map.of("error", "bad request"), HttpStatus.BAD_REQUEST);
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
