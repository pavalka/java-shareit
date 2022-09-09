package ru.practicum.shareit.requests;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.requests.dto.ItemRequestDto;

import java.util.Map;

@Component
public class ItemRequestClient extends BaseClient {
    private static final String REQUESTS_ENDPOINT = "/requests";

    public ItemRequestClient(@Value("${shareit-server.url}") String url, RestTemplateBuilder builder) {
        super(builder.requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .uriTemplateHandler(new DefaultUriBuilderFactory(url + REQUESTS_ENDPOINT))
                .build()
        );
    }

    public ResponseEntity<Object> createNewRequest(long authorId, ItemRequestDto requestDto) {
        return post("", authorId, requestDto);
    }

    public ResponseEntity<Object> getAllRequestsByUser(long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> getAllRequestsPageable(long userId, long from, int size) {
        return get("/all?from={fromVal}&size={sizeVal}", userId, Map.of("fromVal", from, "sizeVal", size));
    }

    public ResponseEntity<Object> getRequestById(long userId, long requestId) {
        return get("/{requestId}", userId, Map.of("requestId", requestId));
    }
}
