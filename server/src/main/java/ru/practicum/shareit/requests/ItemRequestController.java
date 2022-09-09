package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.service.ItemRequestService;

import java.util.Collection;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ItemRequestController {
    private final ItemRequestService requestService;

    @PostMapping
    public ItemRequestDto createNewRequest(@RequestHeader("X-Sharer-User-Id") long authorId,
                                           @RequestBody ItemRequestDto requestDto) {
        return requestService.createNewRequest(authorId, requestDto);
    }

    @GetMapping
    public Collection<ItemRequestDto> getAllRequestsByUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        return requestService.getAllRequestsByUser(userId);
    }

    @GetMapping("/all")
    public Collection<ItemRequestDto> getAllRequestsPageable(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(name = "from") long from,
            @RequestParam(name = "size") int size) {
        return requestService.getAllRequestsPageable(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @PathVariable("requestId") long requestId) {
        return requestService.getRequestById(userId, requestId);
    }
}
