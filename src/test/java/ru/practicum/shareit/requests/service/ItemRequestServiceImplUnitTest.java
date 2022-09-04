package ru.practicum.shareit.requests.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.exceptions.RequestNotFoundException;
import ru.practicum.shareit.requests.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.repository.UserDao;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplUnitTest {
    @Mock
    private UserDao userRepository;

    @Mock
    private ItemRequestRepository requestRepository;

    @InjectMocks
    private ItemRequestServiceImpl requestService;

    @Test
    void createNewRequestThrowsExceptionWhenUserIdIsInvalid() {
        Mockito.when(userRepository.findById(10L)).thenReturn(Optional.empty());

        var ex = Assertions.assertThrows(UserNotFoundException.class,
                () -> requestService.createNewRequest(10L, createRequestDto(1L)));

        Assertions.assertEquals("Пользователь с id = 10 не найден", ex.getMessage());
        Mockito.verify(requestRepository, Mockito.never()).save(Mockito.any(ItemRequest.class));
    }

    @Test
    void createNewRequestSaveRequest() {
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(createUser(1L)));

        Mockito.when(requestRepository.save(Mockito.any(ItemRequest.class)))
                .thenReturn(createRequest(1, createUser(1)));

        var result = Assertions.assertDoesNotThrow(
                () -> requestService.createNewRequest(1, createRequestDto(1)));

        Assertions.assertEquals(1, result.getId());
        Assertions.assertEquals("description 1", result.getDescription());
        Assertions.assertTrue(Objects.nonNull(result.getCreated()));
        Assertions.assertTrue(Objects.isNull(result.getItems()));

        Mockito.verify(requestRepository, Mockito.times(1)).save(Mockito.any(ItemRequest.class));
    }

    @Test
    void getAllRequestsByUserThrowsExceptionWhenUserIdIsInvalid() {
        Mockito.when(userRepository.findById(10L)).thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class, () -> requestService.getAllRequestsByUser(10));
    }

    @Test
    void getAllRequestsPageableThrowsExceptionWhenUserIdIsInvalid() {
        Mockito.when(userRepository.findById(10L)).thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class,
                () -> requestService.getAllRequestsPageable(10, 0, 1));

        Mockito.verify(requestRepository, Mockito.never()).findAllByAuthorIsNot(Mockito.any(User.class),
                Mockito.any(Pageable.class));
    }

    @Test
    void getAllRequestsPageableReturnValue() {
        var user = createUser(1);

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Mockito.when(requestRepository.findAllByAuthorIsNot(Mockito.any(User.class), Mockito.any(Pageable.class)))
                .thenReturn(List.of(createRequest(1, user)));

        var result = Assertions.assertDoesNotThrow(
                () -> requestService.getAllRequestsPageable(1, 0, 1));

        Assertions.assertEquals(1, result.size());

        for (ItemRequestDto requestDto : result) {
            Assertions.assertEquals(1, requestDto.getId());
            Assertions.assertEquals("description 1", requestDto.getDescription());
            Assertions.assertTrue(Objects.nonNull(requestDto.getCreated()));
        }

        Mockito.verify(requestRepository, Mockito.times(1)).findAllByAuthorIsNot(Mockito.any(User.class),
                Mockito.any(Pageable.class));
    }

    @Test
    void getRequestByIdThrowsExceptionWhenUserIdIsInvalid() {
        Mockito.when(userRepository.findById(10L)).thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class, () -> requestService.getRequestById(10, 23));

        Mockito.verify(requestRepository, Mockito.never()).findById(Mockito.anyLong());
    }

    @Test
    void getRequestByIdThrowsExceptionWhenRequestIdIsInvalid() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(createUser(1)));
        Mockito.when(requestRepository.findById(23L)).thenReturn(Optional.empty());

        Assertions.assertThrows(RequestNotFoundException.class,
                () -> requestService.getRequestById(1, 23));
    }

    @Test
    void getRequestByIdReturnValue() {
        var user = createUser(1);

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        var request = createRequest(1, user);

        Mockito.when(requestRepository.findById(1L)).thenReturn(Optional.of(request));

        var result = Assertions.assertDoesNotThrow(() -> requestService.getRequestById(1, 1));

        Assertions.assertEquals(request.getId(), result.getId());
        Assertions.assertEquals(request.getDescription(), result.getDescription());
        Assertions.assertEquals(request.getCreated(), result.getCreated());

        Mockito.verify(requestRepository, Mockito.times(1)).findById(Mockito.anyLong());
    }

    private User createUser(long userId) {
        var user = new User();

        user.setId(userId);
        user.setName("user " + userId);
        user.setEmail(String.format("user%d@email.ru", userId));
        return user;
    }

    private ItemRequestDto createRequestDto(long requestId) {
        var requestDto = new ItemRequestDto();

        requestDto.setId(requestId);
        requestDto.setDescription("description " + requestId);
        return requestDto;
    }

    private ItemRequest createRequest(long requestId, User user) {
        var request = new ItemRequest();

        request.setId(requestId);
        request.setDescription("description " + requestId);
        request.setAuthor(user);
        return request;
    }
}