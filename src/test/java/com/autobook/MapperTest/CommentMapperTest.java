package com.autobook.MapperTest;

import com.autobook.Social.Comment.Comment;
import com.autobook.Social.Comment.CommentMapper;
import com.autobook.Social.Comment.DTO.Response.CommentResponse;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentMapperTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private CommentMapper commentMapper;

    @Test
    void toResponse_ok() {
        User author = new UserTestBuilder().withId(1L).withUsername("andrii").build();
        author.setVisibleName("Andrii");

        Comment comment = new Comment();
        comment.setId(10L);
        comment.setContent("Nice post!");
        comment.setAuthor(author);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setLikeCount(3);

        UserCardResponse card = new UserCardResponse(1L, "Andrii", "andrii", null, null, false);
        when(userMapper.toCardResponse(author)).thenReturn(card);

        CommentResponse response = commentMapper.toResponse(comment);

        assertNotNull(response);
        assertEquals(10L, response.id());
        assertEquals("Nice post!", response.content());
        assertEquals(3, response.likes());
        assertNull(response.parentId());
        assertTrue(response.replies().isEmpty());
    }

    @Test
    void toResponse_withParent() {
        User author = new UserTestBuilder().withId(1L).withUsername("andrii").build();

        Comment parent = new Comment();
        parent.setId(5L);

        Comment comment = new Comment();
        comment.setId(10L);
        comment.setContent("Reply!");
        comment.setAuthor(author);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setParentComment(parent);

        UserCardResponse card = new UserCardResponse(1L, "Andrii", "andrii", null, null, false);
        when(userMapper.toCardResponse(author)).thenReturn(card);

        CommentResponse response = commentMapper.toResponse(comment);

        assertEquals(5L, response.parentId());
    }

    @Test
    void toResponseLevel_returnsNull_whenSeenAlready() {
        User author = new UserTestBuilder().withId(1L).withUsername("andrii").build();
        Comment comment = new Comment();
        comment.setId(10L);
        comment.setContent("Seen!");
        comment.setAuthor(author);

        java.util.List<Long> seenIds = new java.util.ArrayList<>();
        seenIds.add(10L); // already seen

        CommentResponse response = commentMapper.toResponseLevel(comment, seenIds);

        assertNull(response);
    }
}
