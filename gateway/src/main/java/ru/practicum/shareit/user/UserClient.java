package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Map;

@Component
public class UserClient extends BaseClient {
    private static final String BASE_USER_ENDPOINT = "/users";

    public UserClient(@Value("${shareit-server.url}") String baseURI, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(baseURI + BASE_USER_ENDPOINT))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build());
    }

    public ResponseEntity<Object> getAllUsers() {
        return get("");
    }

    public ResponseEntity<Object> getUserById(long userId) {
        return get("/{userId}", null, Map.of("userId", userId));
    }

    public ResponseEntity<Object> createNewUser(UserDto userDto) {
        return post("", userDto);
    }

    public ResponseEntity<Object> updateUser(UserDto userDto) {
        return patch("/{userId}", null, Map.of("userId", userDto.getId()), userDto);
    }

    public ResponseEntity<Object> deleteUser(long userId) {
        return delete("/{userId}", null, Map.of("userId", userId));
    }
}
