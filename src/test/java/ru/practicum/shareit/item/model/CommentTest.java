package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class CommentTest {

    private static Comment originComment;
    private static Comment copyComment;
    private static Comment anotherComment;

    @BeforeAll
    static void beforeAll() {
        User user = User.builder()
                .id(1L)
                .name("userName")
                .email("user@ya.ru")
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("Item")
                .description("Description of item")
                .available(false)
                .owner(user)
                .build();

        originComment = Comment.builder()
                .id(1L)
                .text("Comment 1 on item by user")
                .item(item)
                .author(user)
                .created(LocalDateTime.of(2023, 5, 1, 15, 0, 0))
                .build();

        copyComment = new Comment();
        copyComment.setId(originComment.getId());
        copyComment.setText(originComment.getText());
        copyComment.setItem(originComment.getItem());
        copyComment.setAuthor(originComment.getAuthor());
        copyComment.setCreated(originComment.getCreated());

        anotherComment = Comment.builder()
                .id(2L)
                .text("Comment 2 on item by user")
                .item(item)
                .author(user)
                .created(LocalDateTime.of(2023, 5, 1, 21, 0, 0))
                .build();
    }

    @Test
    void compareEqualComments() {
        boolean equalId = originComment.getId().equals(copyComment.getId());
        assertTrue(equalId);
        boolean equalText = originComment.getText().equals(copyComment.getText());
        assertTrue(equalText);
        boolean equalItem = originComment.getItem().equals(copyComment.getItem());
        assertTrue(equalItem);
        boolean equalAuthor = originComment.getAuthor().equals(copyComment.getAuthor());
        assertTrue(equalAuthor);
        boolean equalCreated = originComment.getCreated().equals(copyComment.getCreated());
        assertTrue(equalCreated);
        boolean equal = originComment.equals(copyComment);
        assertTrue(equal);
    }

    @Test
    void compareDifferentComments() {
        boolean equalId = originComment.getId().equals(anotherComment.getId());
        assertFalse(equalId);
        boolean equalText = originComment.getText().equals(anotherComment.getText());
        assertFalse(equalText);
        boolean equalItem = originComment.getItem().equals(anotherComment.getItem());
        assertTrue(equalItem);
        boolean equalAuthor = originComment.getAuthor().equals(anotherComment.getAuthor());
        assertTrue(equalAuthor);
        boolean equalCreated = originComment.getCreated().equals(anotherComment.getCreated());
        assertFalse(equalCreated);
        boolean equal = originComment.equals(anotherComment);
        assertFalse(equal);

        equalId = copyComment.getId().equals(anotherComment.getId());
        assertFalse(equalId);
        equalText = copyComment.getText().equals(anotherComment.getText());
        assertFalse(equalText);
        equalItem = copyComment.getItem().equals(anotherComment.getItem());
        assertTrue(equalItem);
        equalAuthor = copyComment.getAuthor().equals(anotherComment.getAuthor());
        assertTrue(equalAuthor);
        equalCreated = copyComment.getCreated().equals(anotherComment.getCreated());
        assertFalse(equalCreated);
        equal = copyComment.equals(anotherComment);
        assertFalse(equal);
    }

}