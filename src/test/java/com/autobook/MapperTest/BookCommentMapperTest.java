package com.autobook.MapperTest;

import com.autobook.Library.BookComment.BookComment;
import com.autobook.Library.BookComment.BookCommentMapper;
import com.autobook.Library.BookComment.DTO.Response.BookCommentResponse;
import com.autobook.Social.User.DTO.Response.UserCardResponse;
import com.autobook.Social.User.User;
import com.autobook.Social.User.UserMapper;
import com.autobook.util.UserTestBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookCommentMapperTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private BookCommentMapper bookCommentMapper;

    @Test
    void toResponse_rootComment() {
        User author = new UserTestBuilder().withId(1L).withUsername("andrii").build();
        author.setVisibleName("Andrii");

        BookComment comment = new BookComment();
        comment.setId(10L);
        comment.setContent("Great book!");
        comment.setAuthor(author);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setLikeCount(5);

        UserCardResponse card = new UserCardResponse(1L, "Andrii", "andrii", null, null, false);
        when(userMapper.toCardResponse(author)).thenReturn(card);

        BookCommentResponse response = bookCommentMapper.toResponse(comment);

        assertNotNull(response);
        assertEquals(10L, response.id());
        assertEquals("Great book!", response.content());
        assertEquals(5, response.likes());
        assertNull(response.parentId());
        assertTrue(response.replies().isEmpty());
    }

    @Test
    void toResponse_withParentComment() {
        User author = new UserTestBuilder().withId(1L).withUsername("andrii").build();

        BookComment parent = new BookComment();
        parent.setId(5L);

        BookComment comment = new BookComment();
        comment.setId(20L);
        comment.setContent("Reply to comment");
        comment.setAuthor(author);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setParentComment(parent);

        UserCardResponse card = new UserCardResponse(1L, "Andrii", "andrii", null, null, false);
        when(userMapper.toCardResponse(author)).thenReturn(card);

        BookCommentResponse response = bookCommentMapper.toResponse(comment);

        assertEquals(5L, response.parentId());
    }

    @Test
    void toResponseLevel_returnsNull_ifAlreadySeen() {
        User author = new UserTestBuilder().withId(1L).withUsername("andrii").build();

        BookComment comment = new BookComment();
        comment.setId(10L);
        comment.setContent("Already seen!");
        comment.setAuthor(author);

        List<Long> seenIds = new ArrayList<>();
        seenIds.add(10L); // already seen

        BookCommentResponse response = bookCommentMapper.toResponseLevel(comment, seenIds);

        assertNull(response);
    }
}
