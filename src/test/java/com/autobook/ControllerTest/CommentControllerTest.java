package com.autobook.ControllerTest;

import com.autobook.Social.Comment.CommentController;
import com.autobook.Social.Comment.CommentService;
import com.autobook.Social.Comment.DTO.Request.CreateCommentRequest;
import com.autobook.Social.Comment.DTO.Request.UpdateCommentRequest;
import com.autobook.Social.Comment.DTO.Response.CommentResponse;
import com.autobook.Social.Post.Post;
import com.autobook.Social.Post.PostRepository;
import com.autobook.Social.User.User;
import com.autobook.Social.User.UserRepository;
import com.autobook.util.PostTestBuilder;
import com.autobook.util.UserTestBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentControllerTest {

    @Mock
    private CommentService commentService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private CommentController commentController;

    private Principal createPrincipal(String name) {
        return () -> name;
    }

    @Test
    void createComment() {
        Principal p = createPrincipal("user");
        User user = new UserTestBuilder().withId(1L).withUsername("user").build();
        Post post = new PostTestBuilder().withId(1L).build();

        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        CreateCommentRequest req = new CreateCommentRequest("text", null);
        CommentResponse resp = mock(CommentResponse.class);
        when(commentService.createComment(req, user, post)).thenReturn(resp);

        assertEquals(resp, commentController.createComment(1L, req, p));
    }

    @Test
    void getCommentById() {
        CommentResponse resp = mock(CommentResponse.class);
        when(commentService.getCommentById(1L)).thenReturn(resp);
        assertEquals(resp, commentController.getCommentById(1L));
    }

    @Test
    void getCommentsByPost() {
        Post post = new PostTestBuilder().withId(1L).build();
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(commentService.getCommentsByPost(post)).thenReturn(List.of());
        assertEquals(0, commentController.getCommentsByPost(1L).size());
    }

    @Test
    void getCommentsByAuthor() {
        User user = new UserTestBuilder().withId(1L).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(commentService.getCommentsByAuthor(user)).thenReturn(List.of());
        assertEquals(0, commentController.getCommentsByAuthor(1L).size());
    }

    @Test
    void updateComment() {
        UpdateCommentRequest req = new UpdateCommentRequest("new");
        CommentResponse resp = mock(CommentResponse.class);
        when(commentService.updateCommentContent(1L, req)).thenReturn(resp);
        assertEquals(resp, commentController.updateComment(1L, req));
    }

    @Test
    void likeComment() {
        commentController.likeComment(1L);
        verify(commentService).incrementLikeCount(1L);
    }

    @Test
    void deleteComment() {
        Principal p = createPrincipal("user");
        User user = new UserTestBuilder().withId(1L).withUsername("user").build();
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));

        commentController.deleteComment(1L, p);
        verify(commentService).deleteCommentByIdAndAuthor(1L, user);
    }
}
