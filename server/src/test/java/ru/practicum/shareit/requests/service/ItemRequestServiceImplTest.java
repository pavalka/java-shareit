package ru.practicum.shareit.requests.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserDao;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureTestDatabase
@Sql("classpath:schema-test.sql")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class ItemRequestServiceImplTest {

    private final ItemRequestService requestService;
    private final UserDao userRepository;
    private  final ItemRequestRepository requestRepository;

    @AfterEach
    void cleanUp() {
        userRepository.deleteAll();
        requestRepository.deleteAll();
    }

    @Test
    void createNewRequest() {
        var user = userRepository.save(createUser(1));
        var requestDto = createRequestDto(1);
        var request = requestService.createNewRequest(user.getId(), requestDto);
        var result  = requestRepository.findById(request.getId());

        assertTrue(result.isPresent());
        assertEquals(requestDto.getDescription(), result.get().getDescription());
        assertNotNull(result.get().getCreated());
    }

    @Test
    void getAllRequestsByUser() {
        var userOne = userRepository.save(createUser(1));
        var userTwo = userRepository.save(createUser(2));
        var requestThree = createRequest(3, userTwo);

        requestRepository.save(createRequest(1, userOne));
        requestRepository.save(createRequest(2, userOne));
        requestRepository.save(requestThree);

        var requests = requestService.getAllRequestsByUser(userTwo.getId())
                .toArray(new ItemRequestDto[1]);

        assertEquals(1, requests.length);
        assertEquals(requestThree.getDescription(), requests[0].getDescription());
        assertEquals(requestThree.getCreated(), requests[0].getCreated());
        assertTrue(requests[0].getItems().isEmpty());
    }

    @Test
    void getAllRequestsPageable() {
        var userOne = userRepository.save(createUser(1));
        var userTwo = userRepository.save(createUser(2));

        requestRepository.save(createRequest(1, userOne));
        requestRepository.save(createRequest(2, userTwo));

        var requestThree = createRequest(3, userOne);

        requestRepository.save(requestThree);

        var requests = requestService.getAllRequestsPageable(userTwo.getId(), 0, 1)
                .toArray(new ItemRequestDto[1]);

        assertEquals(1, requests.length);
        assertEquals(requestThree.getDescription(), requests[0].getDescription());
        assertEquals(requestThree.getCreated(), requests[0].getCreated());
        assertTrue(requests[0].getItems().isEmpty());
    }

    @Test
    void getRequestById() {
        var userOne = userRepository.save(createUser(1));
        var userTwo = userRepository.save(createUser(2));
        var requestOne = createRequest(1, userOne);
        var savedRequest = requestRepository.save(requestOne);

        requestRepository.save(createRequest(2, userTwo));

        var result = requestService.getRequestById(userOne.getId(), savedRequest.getId());

        assertEquals(savedRequest.getId(), result.getId());
        assertEquals(requestOne.getDescription(), result.getDescription());
        assertEquals(requestOne.getCreated(), result.getCreated());
        assertTrue(result.getItems().isEmpty());
    }

    private User createUser(long userNumber) {
        var user = new User();

        user.setName("user " + userNumber);
        user.setEmail(String.format("user%d@email.ru", userNumber));
        return user;
    }

    private ItemRequestDto createRequestDto(long requestNumber) {
        var requestDto = new ItemRequestDto();

        requestDto.setDescription("description " + requestNumber);
        return requestDto;
    }

    private ItemRequest createRequest(long requestNumber, User user) {
        var request = new ItemRequest();

        request.setDescription("description " + requestNumber);
        request.setAuthor(user);
        return request;
    }
}