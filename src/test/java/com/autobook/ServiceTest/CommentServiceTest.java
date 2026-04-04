package com.autobook.ServiceTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.autobook.Exception.CommentNotFoundException;
import com.autobook.Exception.EmptyCommentContentException;
import com.autobook.Factory.CommentFactory;
import com.autobook.Social.Comment.Comment;
import com.autobook.Social.Comment.CommentRepository;
import com.autobook.Social.Comment.CommentService;
import com.autobook.Social.Post.Post;
import com.autobook.Social.Post.PostRepository;
import com.autobook.Social.User.User;
import com.autobook.util.CommentTestBuilder;
import com.autobook.util.PostTestBuilder;
import com.autobook.util.UserTestBuilder;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @Mock
    private CommentFactory commentFactory;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private CommentService commentService;

    @Test
    void createComment_ok() {
        User postAuthor = new UserTestBuilder().withId(1L).build();
        User commentAuthor = new UserTestBuilder().withId(2L).build();

        Post post = new PostTestBuilder()
                .withId(10L)
                .withAuthor(postAuthor)
                .build();

        Comment comment = new CommentTestBuilder()
                .withId(1L)
                .withAuthor(commentAuthor)
                .withPostConnected(post)
                .withContent("Good Book!")
                .build();

        when(commentFactory.create("Good Book!", commentAuthor, post)).thenReturn(comment);
        when(commentRepository.save(comment)).thenReturn(comment);

        Comment result = commentService.createComment("Good Book!", commentAuthor, post);

        assertNotNull(result);
        assertEquals("Good Book!", result.getContent());
        assertEquals(1L, result.getId());
        assertEquals(post, result.getPost());
        assertEquals(commentAuthor, result.getAuthor());

        verify(commentFactory).create("Good Book!", commentAuthor, post);
        verify(commentRepository).save(comment);
        verify(postRepository).incrementCommentCount(10L);
    }

    @Test
    void createComment_emptyContent() {
        User author = new UserTestBuilder().build();
        Post post = new PostTestBuilder().withAuthor(author).build();

        assertThrows(
                EmptyCommentContentException.class,
                () -> commentService.createComment(" ", author, post)
        );

        verify(commentFactory, never()).create(any(), any(), any());
        verify(commentRepository, never()).save(any(Comment.class));
        verify(postRepository, never()).incrementCommentCount(anyLong());
    }

    @Test
    void createComment_nullContent() {
        User author = new UserTestBuilder().build();
        Post post = new PostTestBuilder().withAuthor(author).build();

        assertThrows(
                EmptyCommentContentException.class,
                () -> commentService.createComment(null, author, post)
        );

        verify(commentFactory, never()).create(any(), any(), any());
        verify(commentRepository, never()).save(any(Comment.class));
        verify(postRepository, never()).incrementCommentCount(anyLong());
    }

    @Test
    void getCommentById_ok() {
        Comment comment = new CommentTestBuilder()
                .withId(1L)
                .withContent("Hello")
                .build();

        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        Comment result = commentService.getCommentById(1L);

        assertEquals(1L, result.getId());
        assertEquals("Hello", result.getContent());

        verify(commentRepository).findById(1L);
    }

    @Test
    void getCommentById_notFound() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CommentNotFoundException.class, () -> commentService.getCommentById(1L));

        verify(commentRepository).findById(1L);
    }

    @Test
    void getCommentsByAuthor_ok() {
        User author = new UserTestBuilder().build();

        Comment comment1 = new CommentTestBuilder()
                .withId(1L)
                .withContent("Hello")
                .withAuthor(author)
                .build();

        Comment comment2 = new CommentTestBuilder()
                .withId(2L)
                .withContent("Bye")
                .withAuthor(author)
                .build();

        when(commentRepository.findByAuthorOrderByCreatedAtDesc(author))
                .thenReturn(List.of(comment1, comment2));

        List<Comment> result = commentService.getCommentsByAuthor(author);

        assertEquals(2, result.size());
        assertEquals("Hello", result.get(0).getContent());
        assertEquals("Bye", result.get(1).getContent());
        assertEquals(author, result.get(1).getAuthor());

        verify(commentRepository).findByAuthorOrderByCreatedAtDesc(author);
    }

    @Test
    void getCommentsByPost_ok() {
        Post post = new PostTestBuilder().build();

        Comment comment1 = new CommentTestBuilder()
                .withId(1L)
                .withContent("Hello")
                .withPostConnected(post)
                .build();

        Comment comment2 = new CommentTestBuilder()
                .withId(2L)
                .withContent("Bye")
                .withPostConnected(post)
                .build();

        when(commentRepository.findByPostOrderByCreatedAtDesc(post))
                .thenReturn(List.of(comment1, comment2));

        List<Comment> result = commentService.getCommentsByPost(post);

        assertEquals(2, result.size());
        assertEquals("Hello", result.get(0).getContent());
        assertEquals("Bye", result.get(1).getContent());
        assertEquals(post, result.get(1).getPost());

        verify(commentRepository).findByPostOrderByCreatedAtDesc(post);
    }

    @Test
    void updateCommentContent_ok() {
        Comment comment = new CommentTestBuilder()
                .withId(1L)
                .withContent("Old comment")
                .build();

        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(commentRepository.save(comment)).thenReturn(comment);

        Comment result = commentService.updateCommentContent(1L, "New comment");

        assertEquals("New comment", result.getContent());

        verify(commentRepository).findById(1L);
        verify(commentRepository).save(comment);
    }

    @Test
    void updateCommentContent_emptyContent() {
        assertThrows(
                EmptyCommentContentException.class,
                () -> commentService.updateCommentContent(1L, " ")
        );

        verify(commentRepository, never()).findById(anyLong());
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void updateCommentContent_EmptyContent() {
        assertThrows(
                EmptyCommentContentException.class,
                () -> commentService.updateCommentContent(1L, null)
        );

        verify(commentRepository, never()).findById(anyLong());
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void updateCommentContent_notFound() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
                CommentNotFoundException.class,
                () -> commentService.updateCommentContent(1L, "New comment")
        );

        verify(commentRepository).findById(1L);
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void deleteCommentById_ok() {
        Post post = new PostTestBuilder().withId(100L).build();
        Comment comment = new CommentTestBuilder()
                .withId(1L)
                .withPostConnected(post)
                .build();

        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        commentService.deleteCommentById(1L);

        verify(commentRepository).findById(1L);
        verify(postRepository).decrementCommentCount(100L);
        verify(commentRepository).delete(comment);
    }

    @Test
    void deleteCommentById_notFound() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
                CommentNotFoundException.class,
                () -> commentService.deleteCommentById(1L)
        );

        verify(commentRepository).findById(1L);
        verify(postRepository, never()).decrementCommentCount(anyLong());
        verify(commentRepository, never()).delete(any(Comment.class));
    }

    @Test
    void deleteCommentByIdAndAuthor_ok() {
        User author = new UserTestBuilder().withId(5L).build();
        Post post = new PostTestBuilder().withId(100L).build();

        Comment comment = new CommentTestBuilder()
                .withId(1L)
                .withAuthor(author)
                .withPostConnected(post)
                .build();

        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        commentService.deleteCommentByIdAndAuthor(1L, author);

        verify(commentRepository).findById(1L);
        verify(postRepository).decrementCommentCount(100L);
        verify(commentRepository).delete(comment);
    }

    @Test
    void deleteCommentByIdAndAuthor_notFound() {
        User author = new UserTestBuilder().withId(5L).build();

        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
                CommentNotFoundException.class,
                () -> commentService.deleteCommentByIdAndAuthor(1L, author)
        );

        verify(commentRepository).findById(1L);
        verify(postRepository, never()).decrementCommentCount(anyLong());
        verify(commentRepository, never()).delete(any(Comment.class));
    }
}