package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.IncomingItemDto;

import java.util.Map;

@Component
public class ItemClient extends BaseClient {
    private static final String ITEM_ENDPOINT = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String url, RestTemplateBuilder builder) {
        super(builder.requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .uriTemplateHandler(new DefaultUriBuilderFactory(url + ITEM_ENDPOINT))
                .build()
        );
    }

    public ResponseEntity<Object> getItemByIdAndUser(long userId, long itemId) {
        return get("/{itemId}", userId, Map.of("itemId", itemId));
    }

    public ResponseEntity<Object> getAllItemsForUser(long userId, long from, int size) {
        return get("?from={fromVal}&size={sizeVal}", userId, Map.of("fromVal", from, "sizeVal", size));
    }

    public ResponseEntity<Object> createNewItem(long ownerId, IncomingItemDto itemDto) {
        return post("", ownerId, itemDto);
    }

    public ResponseEntity<Object> updateItem(long ownerId, IncomingItemDto itemDto) {
        return patch("/{itemId}", ownerId, Map.of("itemId", itemDto.getId()), itemDto);
    }

    public ResponseEntity<Object> findItemsByNameAndDescription(String text, long from, int size) {
        return get("/search?text={textVal}&from={fromVal}&size={sizeVal}", null, Map.of("textVal", text, "fromVal", from,
                "sizeVal", size));
    }

    public ResponseEntity<Object> createComment(long userId, long itemId, CommentDto commentDto) {
        return post("/{itemId}/comment", userId, Map.of("itemId", itemId), commentDto);
    }
}
