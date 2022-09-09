package ru.practicum.shareit.requests.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.PageableByOffsetAndSize;
import ru.practicum.shareit.requests.ItemRequestMapper;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.exceptions.RequestNotFoundException;
import ru.practicum.shareit.requests.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.repository.UserDao;

import java.util.Collection;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserDao userRepository;
    private final ItemRequestRepository requestRepository;

    @Override
    @Transactional
    public ItemRequestDto createNewRequest(long authorId, ItemRequestDto requestDto) {
        var author = getUserById(authorId);
        var request = ItemRequestMapper.mapToItemRequest(requestDto, author);

        return ItemRequestMapper.mapToItemRequestDto(requestRepository.save(request));
    }

    @Override
    public Collection<ItemRequestDto> getAllRequestsByUser(long userId) {
        var user = getUserById(userId);

        return ItemRequestMapper.mapToItemRequestDtoCollection(user.getRequests());
    }

    @Override
    public Collection<ItemRequestDto> getAllRequestsPageable(long userId, long from, int size) {
        var user = getUserById(userId);
        var pageable = new PageableByOffsetAndSize(from, size, Sort.by(Sort.Direction.DESC, "created"));

        return ItemRequestMapper.mapToItemRequestDtoCollection(requestRepository.findAllByAuthorIsNot(user, pageable));
    }

    @Override
    public ItemRequestDto getRequestById(long userId, long requestId) {
        getUserById(userId);
        return ItemRequestMapper.mapToItemRequestDto(requestRepository.findById(requestId).orElseThrow(
                () -> new RequestNotFoundException(String.format("Запрос с id = %d не найден", requestId))
        ));
    }

    private User getUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException(String.format("Пользователь с id = %d не найден", userId))
        );
    }
}
