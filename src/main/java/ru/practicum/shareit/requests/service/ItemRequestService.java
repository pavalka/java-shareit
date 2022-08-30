package ru.practicum.shareit.requests.service;

import ru.practicum.shareit.requests.dto.ItemRequestDto;

import java.util.Collection;

public interface ItemRequestService {
    ItemRequestDto createNewRequest(long authorId, ItemRequestDto requestDto);

    Collection<ItemRequestDto> getAllRequestsByUser(long userId);

    Collection<ItemRequestDto> getAllRequestsPageable(long userId, long from, int size);

    ItemRequestDto getRequestById(long userId, long requestId);
}
