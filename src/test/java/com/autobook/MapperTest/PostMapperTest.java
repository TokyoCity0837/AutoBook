package com.autobook.MapperTest;

import com.autobook.Social.Comment.CommentMapper;
import com.autobook.Social.Post.DTO.Response.PostDetailsResponse;
import com.autobook.Social.Post.DTO.Response.PostResponse;
import com.autobook.Social.Post.Post;
import com.autobook.Social.Post.PostMapper;
import com.autobook.Social.User.DTO.Response.UserCardResponse;
import com.autobook.Social.User.DTO.Response.UserPostDetailsResponse;
import com.autobook.Social.User.User;
import com.autobook.Social.User.UserMapper;
import com.autobook.util.UserTestBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostMapperTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private PostMapper postMapper;

    @Test
    void toResponse() {
        User author = new UserTestBuilder().withId(1L).withUsername("andrii").build();
        Post post = new Post();
        post.setId(50L);
        post.setContent("Post Content");
        post.setAuthor(author);

        UserCardResponse userCard = new UserCardResponse(1L, "Andrii", "andrii", null, null, false);
        when(userMapper.toCardResponse(author)).thenReturn(userCard);

        PostResponse response = postMapper.toResponse(post, true, false);

        assertNotNull(response);
        assertEquals(50L, response.id());
        assertEquals("Post Content", response.content());
        assertEquals(true, response.likedByMe());
        assertEquals(false, response.repostedByMe());
    }

    @Test
    void toDetailsResponse() {
        User author = new UserTestBuilder().withId(1L).withUsername("author").build();
        Post post = new Post();
        post.setId(50L);
        post.setContent("Post content details");
        post.setAuthor(author);
        post.setComments(Collections.emptyList());

        UserPostDetailsResponse userPostDetails = new UserPostDetailsResponse(1L, "Author", null, null, null);
        when(userMapper.toPostDetailsResponse(author)).thenReturn(userPostDetails);

        PostDetailsResponse response = postMapper.toDetailsResponse(post, false, true);

        assertNotNull(response);
        assertEquals(50L, response.id());
        assertEquals("Post content details", response.content());
        assertEquals(false, response.likedByMe());
        assertEquals(true, response.repostedByMe());
    }
}
