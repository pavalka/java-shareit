package ru.practicum.shareit.requests;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.requests.dto.ItemDtoForRequest;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemRequestMapperTest {

    @Test
    void mapToItemRequestDtoReturnNullWhenItemRequestIsNull() {
        assertNull(ItemRequestMapper.mapToItemRequestDto(null));
    }

    @Test
    void mapToItemRequestDtoReturnItemRequestDto() {
        var user = createUser(1);
        var item = createItem(1, user);
        var request = createRequest(1, user, item);
        var result = ItemRequestMapper.mapToItemRequestDto(request);

        assertEquals(request.getId(), result.getId());
        assertEquals(request.getDescription(), result.getDescription());
        assertEquals(request.getCreated(), result.getCreated());
        assertEquals(request.getItems().size(), result.getItems().size());

        for (ItemDtoForRequest itemDto : result.getItems()) {
            assertEquals(item.getId(), itemDto.getId());
            assertEquals(item.getName(), itemDto.getName());
            assertEquals(item.getDescription(), itemDto.getDescription());
            assertEquals(item.getAvailable(), itemDto.getAvailable());
            assertEquals(item.getOwner().getId(), itemDto.getOwnerId());
            assertEquals(request.getId(), itemDto.getRequestId());
        }
    }


    @Test
    void mapToItemRequestReturnNullWhenItemRequestDtoIsNull() {
        assertNull(ItemRequestMapper.mapToItemRequest(null, createUser(1)));
    }

    @Test
    void mapToItemRequestReturnItemRequest() {
        var user = createUser(1);
        var requestDto = createRequestDto(1);
        var request = ItemRequestMapper.mapToItemRequest(requestDto, user);

        assertNull(request.getId());
        assertEquals(requestDto.getDescription(), request.getDescription());
        assertNull(request.getItems());
        assertNotNull(request.getCreated());
    }

    @Test
    void mapToItemRequestDtoCollectionReturnNullWhenItemRequestCollectionIsNull() {
        assertNull(ItemRequestMapper.mapToItemRequestDtoCollection(null));
    }

    @Test
    void mapToItemRequestDtoCollectionReturnItemRequestDtoCollection() {
        var user = createUser(1);
        var requestOne = createRequest(1, user, createItem(1, user));
        var requestTwo = createRequest(2, user, createItem(2, user));
        var reqList = List.of(requestOne, requestTwo);

        var reqDtoCollection = ItemRequestMapper.mapToItemRequestDtoCollection(reqList);
        int i = 0;

        assertEquals(reqList.size(), reqDtoCollection.size());
        for (ItemRequestDto currentRequestDto : reqDtoCollection) {

            var currentRequest = reqList.get(i);

            assertEquals(currentRequest.getId(), currentRequestDto.getId());
            assertEquals(currentRequest.getDescription(), currentRequestDto.getDescription());
            assertEquals(currentRequest.getCreated(), currentRequestDto.getCreated());
            assertEquals(currentRequest.getItems().size(), currentRequestDto.getItems().size());
            assertEquals(currentRequest.getItems().get(0).getId(), currentRequestDto.getItems().get(0).getId());
            assertEquals(currentRequest.getItems().get(0).getName(), currentRequestDto.getItems().get(0).getName());
            assertEquals(currentRequest.getItems().get(0).getDescription(),
                    currentRequestDto.getItems().get(0).getDescription());
            assertEquals(currentRequest.getItems().get(0).getAvailable(),
                    currentRequestDto.getItems().get(0).getAvailable());
            assertEquals(currentRequest.getItems().get(0).getOwner().getId(),
                    currentRequestDto.getItems().get(0).getOwnerId());
            assertEquals(currentRequest.getItems().get(0).getRequest().getId(),
                    currentRequestDto.getItems().get(0).getRequestId());

            i++;
        }
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

    private ItemRequest createRequest(long requestId, User user, Item item) {
        var request = new ItemRequest();

        request.setId(requestId);
        request.setDescription("description " + requestId);
        request.setAuthor(user);
        request.setItems(List.of(item));
        item.setRequest(request);
        return request;
    }

    private Item createItem(long itemId, User user) {
        var item = new Item();

        item.setId(itemId);
        item.setName("item " + itemId);
        item.setDescription("description " + itemId);
        item.setAvailable(true);
        item.setOwner(user);
        return item;
    }
}