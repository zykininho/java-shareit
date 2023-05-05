package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentMapperTest {

    @Autowired
    CommentMapper commentMapper;

    private static Comment comment;
    private static CommentDto commentDto;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .id(1L)
                .name("user")
                .email("user@ya.ru")
                .build();

        User anotherUser = User.builder()
                .id(2L)
                .name("Another user")
                .email("another_user@ya.ru")
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("Item 1")
                .description("Description of item 1")
                .available(false)
                .owner(user)
                .build();

        comment = Comment.builder()
                .id(1L)
                .text("Comment 1 on item by Another ser")
                .item(item)
                .author(anotherUser)
                .created(LocalDateTime.of(2023, 6, 5, 15, 0, 0))
                .build();
        commentDto = commentMapper.toCommentDto(comment);
    }

    @Test
    void createCommentDtoFromComment() {
        CommentDto commentDtoFromMapper = CommentMapper.INSTANCE.toCommentDto(comment);
        assertEquals(commentDto, commentDtoFromMapper);
    }

    @Test
    void createCommentFromCommentDto() {
        Comment commentFromMapper = CommentMapper.INSTANCE.toComment(commentDto);
        assertEquals(comment, commentFromMapper);
    }

}