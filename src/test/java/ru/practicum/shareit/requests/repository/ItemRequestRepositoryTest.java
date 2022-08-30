package ru.practicum.shareit.requests.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.PageableByOffsetAndSize;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserDao;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class ItemRequestRepositoryTest {

    private final ItemRequestRepository requestRepository;
    private final UserDao userRepository;

    private ItemRequest requestOneUserTwo;
    private ItemRequest requestTwoUserTwo;
    private ItemRequest requestThreeUserTwo;

    private User userOne;

    @BeforeEach
    public void setUp() {
        userOne = userRepository.save(createUser(1));
        var userTwo = userRepository.save(createUser(2));

        requestRepository.save(createRequest(1, userOne));
        requestOneUserTwo = requestRepository.save(createRequest(2, userTwo));
        requestRepository.save(createRequest(3, userOne));
        requestTwoUserTwo = requestRepository.save(createRequest(4, userTwo));
        requestRepository.save(createRequest(5, userOne));
        requestThreeUserTwo = requestRepository.save(createRequest(6, userTwo));
    }

    @Test
    void findAllByAuthorIsNotReturnOneRequestForUserTwo() {
        var pageable = new PageableByOffsetAndSize(0, 1, Sort.unsorted());

        var requests = requestRepository.findAllByAuthorIsNot(userOne, pageable);

        assertEquals(1, requests.size());
        assertEquals(requestOneUserTwo.getId(), requests.get(0).getId());
        assertEquals(requestOneUserTwo.getDescription(), requests.get(0).getDescription());
        assertEquals(requestOneUserTwo.getCreated(), requests.get(0).getCreated());
        assertEquals(requestOneUserTwo.getAuthor().getId(), requests.get(0).getAuthor().getId());
        assertEquals(requestOneUserTwo.getAuthor().getName(), requests.get(0).getAuthor().getName());
        assertEquals(requestOneUserTwo.getAuthor().getEmail(), requests.get(0).getAuthor().getEmail());
    }

    @Test
    void findAllByAuthorIsNotReturnTwoRequestForUserTwo() {
        var pageable = new PageableByOffsetAndSize(1, 2, Sort.unsorted());

        var requests = requestRepository.findAllByAuthorIsNot(userOne, pageable);

        assertEquals(2, requests.size());
        assertEquals(requestTwoUserTwo.getId(), requests.get(0).getId());
        assertEquals(requestTwoUserTwo.getDescription(), requests.get(0).getDescription());
        assertEquals(requestTwoUserTwo.getCreated(), requests.get(0).getCreated());
        assertEquals(requestTwoUserTwo.getAuthor().getId(), requests.get(0).getAuthor().getId());
        assertEquals(requestTwoUserTwo.getAuthor().getName(), requests.get(0).getAuthor().getName());
        assertEquals(requestTwoUserTwo.getAuthor().getEmail(), requests.get(0).getAuthor().getEmail());

        assertEquals(requestThreeUserTwo.getId(), requests.get(1).getId());
        assertEquals(requestThreeUserTwo.getDescription(), requests.get(1).getDescription());
        assertEquals(requestThreeUserTwo.getCreated(), requests.get(1).getCreated());
        assertEquals(requestThreeUserTwo.getAuthor().getId(), requests.get(1).getAuthor().getId());
        assertEquals(requestThreeUserTwo.getAuthor().getName(), requests.get(1).getAuthor().getName());
        assertEquals(requestThreeUserTwo.getAuthor().getEmail(), requests.get(1).getAuthor().getEmail());
    }

    private User createUser(long userNumber) {
        var user = new User();

        user.setName("user " + userNumber);
        user.setEmail(String.format("user%d@email.ru", userNumber));
        return user;
    }

    private ItemRequest createRequest(long requestNumber, User user) {
        var request = new ItemRequest();

        request.setDescription("description " + requestNumber);
        request.setAuthor(user);
        return request;
    }
}