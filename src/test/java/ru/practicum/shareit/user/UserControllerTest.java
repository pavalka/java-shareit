package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class UserControllerTest {
    private long nextId = 1;
    private final MockMvc mvc;
    private final ObjectMapper mapper;

    @MockBean
    private UserServiceImpl userService;

    @Test
    void getAllUsersReturnStatus200AndUserDto() throws Exception {
        var userDto = createUserDtoObj();

        Mockito.when(userService.getAllUsers())
                .thenReturn(List.of(userDto));

        mvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$[0].id").value(userDto.getId()))
                .andExpect(jsonPath("$[0].name").value(userDto.getName()))
                .andExpect(jsonPath("$[0].email").value(userDto.getEmail()));
    }

    @Test
    void getUserByIdReturnStatus200AndUserDto() throws Exception {
        var userDto = createUserDtoObj();

        Mockito.when(userService.getUserById(1))
                .thenReturn(userDto);

        mvc.perform(get("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));
    }

    @Test
    void getUserByIdReturnStatus404WhenUserIdIsInvalid() throws Exception {
        var errMsg = "Пользователь не найден";

        Mockito.when(userService.getUserById(10))
                .thenThrow(new UserNotFoundException(errMsg));

        mvc.perform(get("/users/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(errMsg));
    }

    @Test
    void createNewUserReturnStatus200AndUserDto() throws Exception {
        var userDto = createUserDtoObj();

        Mockito.when(userService.createUser(Mockito.any(UserDto.class)))
                .thenReturn(userDto);

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));
    }

    @Test
    void createNewUserReturnStatus400WhenNameIsEmpty() throws Exception {
        var userDto = createUserDtoObj();

        userDto.setName("");

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    void createNewUserReturnStatus400WhenEmailIsEmpty() throws Exception {
        var userDto = createUserDtoObj();

        userDto.setEmail("");

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    void updateUserReturnStatus200AndUSerDto() throws Exception {
        var userDto = createUserDtoObj();

        Mockito.when(userService.updateUser(Mockito.any(UserDto.class)))
                .thenReturn(userDto);

        mvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));
    }

    @Test
    void updateUserReturnStatus400WhenNameIsEmpty() throws Exception {
        var userDto = createUserDtoObj();

        userDto.setName("");
        userDto.setEmail(null);
        mvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    void updateUserReturnStatus400WhenEmailIsEmpty() throws Exception {
        var userDto = createUserDtoObj();

        userDto.setName(null);
        userDto.setEmail("");
        mvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    void deleteUserReturnStatus200() throws Exception {
        Mockito.doNothing()
                .when(userService).deleteUser(1);
        mvc.perform(delete("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void deleteUserReturnStatus404() throws Exception {
        var errMsg = "Пользователь не найден";

        Mockito.doThrow(new UserNotFoundException(errMsg))
                .when(userService).deleteUser(1);
        mvc.perform(delete("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(errMsg));
    }

    private long getNextId() {
        return nextId++;
    }

    private UserDto createUserDtoObj() {
        var userDto = new UserDto();
        var userId = getNextId();

        userDto.setId(userId);
        userDto.setName("user " + userId);
        userDto.setEmail(String.format("user%d@email.ru", userId));
        return userDto;
    }
}