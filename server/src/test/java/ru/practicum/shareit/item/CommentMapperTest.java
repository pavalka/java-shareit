package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CommentMapperTest {

    private long nextUserId = 1;
    private long nextItemId = 1;
    private long nextCommentId = 1;
    private LocalDateTime nextDateTime = LocalDateTime.of(2022, 8, 25, 10, 23, 0);

    @Test
    void mapCommentToDtoReturnNullWhenArgumentIsNull() {
        assertNull(CommentMapper.mapCommentToDto(null));
    }

    @Test
    void mapCommentToDtoReturnCommentDto() {
        var itemOwner = createUser();
        var comment = createComment(createUser(), createItem(itemOwner), getNextDateTime(), "Text 1");

        var commentDto = CommentMapper.mapCommentToDto(comment);

        assertEquals(comment.getId(), commentDto.getId());
        assertEquals(comment.getUser().getName(), commentDto.getAuthorName());
        assertEquals(comment.getCreationDate(), commentDto.getCreated());
        assertEquals(comment.getText(), commentDto.getText());
    }

    @Test
    void mapCommentsToDtoReturnNullWhenArgumentNull() {
        assertNull(CommentMapper.mapCommentsToDto(null));
    }

    @Test
    void mapCommentsToDtoReturnCommentsDto() {
        var itemOwnerOne = createUser();
        var itemOwnerTwo = createUser();
        var commentOne = createComment(createUser(), createItem(itemOwnerOne), getNextDateTime(), "Text 1");
        var commentTwo = createComment(createUser(), createItem(itemOwnerTwo), getNextDateTime(), "Text 2");

        var commentDto = CommentMapper.mapCommentsToDto(List.of(commentOne, commentTwo))
                .toArray(new CommentDto[1]);

        assertEquals(2, commentDto.length);

        assertEquals(commentOne.getId(), commentDto[0].getId());
        assertEquals(commentOne.getUser().getName(), commentDto[0].getAuthorName());
        assertEquals(commentOne.getCreationDate(), commentDto[0].getCreated());
        assertEquals(commentOne.getText(), commentDto[0].getText());
        assertEquals(commentTwo.getId(), commentDto[1].getId());
        assertEquals(commentTwo.getUser().getName(), commentDto[1].getAuthorName());
        assertEquals(commentTwo.getCreationDate(), commentDto[1].getCreated());
        assertEquals(commentTwo.getText(), commentDto[1].getText());
    }

    @Test
    void mapDtoToCommentReturnNullWhenCommentDtoArgumentIsNull() {
        var itemOwner = createUser();

        assertNull(CommentMapper.mapDtoToComment(null, createItem(itemOwner), createUser()));
    }

    @Test
    void mapDtoToCommentReturnComment() {
        var itemOwner = createUser();
        var user = createUser();
        var item = createItem(itemOwner);
        var commentDto = createCommentDto(user, "Text");

        var comment = CommentMapper.mapDtoToComment(commentDto, item, user);

        assertEquals(commentDto.getText(), comment.getText());
        assertEquals(user.getId(), comment.getUser().getId());
        assertEquals(user.getName(), comment.getUser().getName());
        assertEquals(user.getEmail(), comment.getUser().getEmail());
        assertEquals(item.getId(), comment.getItem().getId());
        assertEquals(item.getName(), comment.getItem().getName());
        assertEquals(item.getDescription(), comment.getItem().getDescription());
        assertEquals(item.getAvailable(), comment.getItem().getAvailable());
    }

    private long getNextUserId() {
        return nextUserId++;
    }

    private long getNextItemId() {
        return nextItemId++;
    }

    private long getNextCommentId() {
        return nextCommentId++;
    }

    private LocalDateTime getNextDateTime() {
        nextDateTime = nextDateTime.plusDays(1);
        return nextDateTime;
    }

    private Item createItem(User owner) {
        var item = new Item();
        var itemId = getNextItemId();

        item.setId(itemId);
        item.setName("Item " + itemId);
        item.setDescription("Item description " + itemId);
        item.setAvailable(true);
        item.setOwner(owner);
        return item;
    }

    private User createUser() {
        var user = new User();
        var userId = getNextUserId();

        user.setId(userId);
        user.setName("user " + userId);
        user.setEmail(String.format("user%d@email.ru", userId));
        return user;
    }

    private Comment createComment(User author, Item item, LocalDateTime created, String text) {
        var comment = new Comment();
        var commentId = getNextCommentId();

        comment.setId(commentId);
        comment.setUser(author);
        comment.setItem(item);
        comment.setCreationDate(created);
        comment.setText(text);
        return comment;
    }

    private CommentDto createCommentDto(User user, String text) {
        var commentDto = new CommentDto();

        commentDto.setId(getNextCommentId());
        commentDto.setAuthorName(user.getName());
        commentDto.setText(text);
        return commentDto;
    }
}