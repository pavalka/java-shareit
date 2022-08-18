package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentMapper {
    public static CommentDto mapCommentToDto(Comment comment) {
        if (comment == null) {
            return null;
        }

        var commentDto = new CommentDto();

        commentDto.setId(comment.getId());
        commentDto.setAuthorName(comment.getUser().getName());
        commentDto.setCreated(comment.getCreationDate());
        commentDto.setText(comment.getText());
        return commentDto;
    }

    public static Collection<CommentDto> mapCommentsToDto(Collection<Comment> comments) {
        if (comments == null) {
            return null;
        }
        return comments.stream().map(CommentMapper::mapCommentToDto).collect(Collectors.toCollection(ArrayList::new));
    }

    public static Comment mapDtoToComment(CommentDto commentDto, Item item, User user) {
        if (commentDto == null) {
            return null;
        }

        var comment = new Comment();

        comment.setUser(user);
        comment.setItem(item);
        comment.setText(commentDto.getText());

        return comment;
    }
}
