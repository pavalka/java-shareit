package ru.practicum.shareit.booking;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.shareit.ErrorMessage;

@ControllerAdvice(assignableTypes = {BookingController.class})
public class BookingExceptionsHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    @ResponseBody
    public ErrorMessage handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex,
                                                                  WebRequest webRequest) {
        if (ex.getParameter().getParameterType() == BookingState.class) {
            return new ErrorMessage("Unknown state: " + webRequest.getParameter("state"));
        }

        return new ErrorMessage("Parameter " + ex.getName() + "should has the type " + ex.getParameter().getParameterType()
                .getName());
    }
}
