package ru.practicum.shareit.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.exceptions.RequestNotFoundException;
import ru.practicum.shareit.requests.service.ItemRequestService;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class ItemRequestControllerTest {
    private static final LocalDateTime CREATE_TIME = LocalDateTime.of(2022, 8, 25, 18, 10, 23);

    private final MockMvc mvc;
    private final ObjectMapper mapper;

    @MockBean
    private ItemRequestService requestService;

    @Test
    void createNewRequestReturnStatus404WhenUserIdIsInvalid() throws Exception {
        var errMsg = "Пользователь с id = 10 не найден";

        Mockito.when(requestService.createNewRequest(Mockito.anyLong(), Mockito.any(ItemRequestDto.class)))
                .thenThrow(new UserNotFoundException(errMsg));

        mvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 10L)
                        .content(mapper.writeValueAsString(createRequestDto(1))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(errMsg));
    }

    @Test
    void createNewRequestReturnStatus200() throws Exception {
        var requestDto = createFilledRequestDto(1);

        Mockito.when(requestService.createNewRequest(Mockito.anyLong(), Mockito.any(ItemRequestDto.class)))
                .thenReturn(requestDto);

        mvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(createRequestDto(1))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestDto.getId()))
                .andExpect(jsonPath("$.description").value(requestDto.getDescription()))
                .andExpect(jsonPath("$.created").value(requestDto.getCreated()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(jsonPath("$.items", IsNull.nullValue()));
    }

    @Test
    void getAllRequestsByUserReturnStatus404WhenUserIdIsInvalid() throws Exception {
        var errMsg = "Пользователь с id = 10 не найден";

        Mockito.when(requestService.getAllRequestsByUser(10)).thenThrow(new UserNotFoundException(errMsg));

        mvc.perform(get("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 10L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(errMsg));
    }

    @Test
    void getAllRequestsByUserReturnStatus200() throws Exception {
        var requestDtoOne = createFilledRequestDto(1);
        var requestDtoTwo = createFilledRequestDto(2);
        var createdForRequestDtoTwo = CREATE_TIME.plusHours(1);

        requestDtoTwo.setCreated(createdForRequestDtoTwo);

        Mockito.when(requestService.getAllRequestsByUser(1)).thenReturn(List.of(requestDtoTwo, requestDtoOne));

        mvc.perform(get("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(requestDtoTwo.getId()))
                .andExpect(jsonPath("$[0].description").value(requestDtoTwo.getDescription()))
                .andExpect(jsonPath("$[0].created").value(requestDtoTwo.getCreated()
                                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(jsonPath("$[0].items", IsNull.nullValue()))
                .andExpect(jsonPath("$[1].id").value(requestDtoOne.getId()))
                .andExpect(jsonPath("$[1].description").value(requestDtoOne.getDescription()))
                .andExpect(jsonPath("$[1].created").value(requestDtoOne.getCreated()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(jsonPath("$[1].items", IsNull.nullValue()));
    }

    @Test
    void getAllRequestsPageableReturnStatus404WhenUserIdIsInvalid() throws Exception {
        var errMsg = "Пользователь с id = 10 не найден";

        Mockito.when(requestService.getAllRequestsPageable(10, 0, 2)).thenThrow(new UserNotFoundException(errMsg));

        mvc.perform(get("/requests/all?from=0&size=2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 10L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(errMsg));
    }

    @Test
    void getAllRequestsPageableReturnStatus200() throws Exception {
        var requestDtoTwo = createFilledRequestDto(2);

        Mockito.when(requestService.getAllRequestsPageable(1, 0, 1)).thenReturn(List.of(requestDtoTwo));

        mvc.perform(get("/requests/all?from=0&size=1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(requestDtoTwo.getId()))
                .andExpect(jsonPath("$[0].description").value(requestDtoTwo.getDescription()))
                .andExpect(jsonPath("$[0].created").value(requestDtoTwo.getCreated()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(jsonPath("$[0].items", IsNull.nullValue()));
    }

    @Test
    void getRequestByIdReturnStatus404WhenUserIdIsInvalid() throws Exception {
        var errMsg = "Пользователь с id = 10 не найден";

        Mockito.when(requestService.getRequestById(10, 1)).thenThrow(new UserNotFoundException(errMsg));

        mvc.perform(get("/requests/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 10L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(errMsg));
    }

    @Test
    void getRequestByIdReturnStatus200() throws Exception {
        var requestDto = createFilledRequestDto(1);

        Mockito.when(requestService.getRequestById(1, 1)).thenReturn(requestDto);

        mvc.perform(get("/requests/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(requestDto.getId()))
                .andExpect(jsonPath("$.description").value(requestDto.getDescription()))
                .andExpect(jsonPath("$.created").value(requestDto.getCreated()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(jsonPath("$.items", IsNull.nullValue()));
    }

    @Test
    void getRequestByIdReturnStatus404WhenRequestIdIsInvalid() throws Exception {
        var errMsg = "Запрос с id = 10 не найден";

        Mockito.when(requestService.getRequestById(1, 10)).thenThrow(new RequestNotFoundException(errMsg));

        mvc.perform(get("/requests/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(errMsg));
    }

    private ItemRequestDto createRequestDto(int requestNum) {
        var requestDto = new ItemRequestDto();

        requestDto.setDescription("description " + requestNum);
        return requestDto;
    }

    private ItemRequestDto createFilledRequestDto(int requestNum) {
        var requestDto = createRequestDto(requestNum);

        requestDto.setCreated(CREATE_TIME);
        requestDto.setId(requestNum);
        return requestDto;
    }
}