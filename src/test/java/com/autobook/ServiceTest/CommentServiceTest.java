package com.autobook.ServiceTest;

import com.autobook.Exception.CommentNotFoundException;
import com.autobook.Exception.EmptyCommentContentException;
import com.autobook.Factory.CommentFactory;
import com.autobook.Social.Comment.Comment;
import com.autobook.Social.Comment.CommentMapper;
import com.autobook.Social.Comment.CommentRepository;
import com.autobook.Social.Comment.CommentService;
import com.autobook.Social.Comment.DTO.Request.CreateCommentRequest;
import com.autobook.Social.Comment.DTO.Request.UpdateCommentRequest;
import com.autobook.Social.Comment.DTO.Response.CommentResponse;
import com.autobook.Social.Post.Post;
import com.autobook.Social.Post.PostRepository;
import com.autobook.Social.User.DTO.Response.UserCardResponse;
import com.autobook.Social.User.User;
import com.autobook.util.CommentTestBuilder;
import com.autobook.util.PostTestBuilder;
import com.autobook.util.UserTestBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

        @Mock
        private CommentFactory commentFactory;

        @Mock
        private CommentRepository commentRepository;

        @Mock
        private PostRepository postRepository;

        @Mock
        private CommentMapper commentMapper;

        @InjectMocks
        private CommentService commentService;

        @Test
        void createComment_ok() {
                User postAuthor = new UserTestBuilder()
                                .withId(1L)
                                .withVisibleName("Post Author")
                                .build();

                User commentAuthor = new UserTestBuilder()
                                .withId(2L)
                                .withVisibleName("Comment Author")
                                .withProfileImage("avatar.png")
                                .build();

                Post post = new PostTestBuilder()
                                .withId(10L)
                                .withAuthor(postAuthor)
                                .build();

                CreateCommentRequest request = new CreateCommentRequest("Good Book!", null);

                Comment comment = new CommentTestBuilder()
                                .withId(1L)
                                .withAuthor(commentAuthor)
                                .withPostConnected(post)
                                .withContent("Good Book!")
                                .build();

                CommentResponse response = buildCommentResponse(comment);

                when(commentFactory.create("Good Book!", commentAuthor, post)).thenReturn(comment);
                when(commentRepository.save(comment)).thenReturn(comment);
                when(commentMapper.toResponse(comment)).thenReturn(response);

                CommentResponse result = commentService.createComment(request, commentAuthor, post);

                assertNotNull(result);
                assertEquals(1L, result.id());
                assertEquals("Good Book!", result.content());
                assertEquals("Comment Author", result.author().visibleName());

                verify(commentFactory).create("Good Book!", commentAuthor, post);
                verify(commentRepository).save(comment);
                verify(postRepository).incrementCommentCount(10L);
                verify(commentMapper).toResponse(comment);
        }

        @Test
        void createComment_emptyContent() {
                User author = new UserTestBuilder().build();
                Post post = new PostTestBuilder().withAuthor(author).build();
                CreateCommentRequest request = new CreateCommentRequest(" ", null);

                assertThrows(
                                EmptyCommentContentException.class,
                                () -> commentService.createComment(request, author, post));

                verify(commentFactory, never()).create(any(), any(), any());
                verify(commentRepository, never()).save(any(Comment.class));
                verify(postRepository, never()).incrementCommentCount(anyLong());
                verify(commentMapper, never()).toResponse(any(Comment.class));
        }

        @Test
        void createComment_nullContent() {
                User author = new UserTestBuilder().build();
                Post post = new PostTestBuilder().withAuthor(author).build();
                CreateCommentRequest request = new CreateCommentRequest(null, null);

                assertThrows(
                                EmptyCommentContentException.class,
                                () -> commentService.createComment(request, author, post));

                verify(commentFactory, never()).create(any(), any(), any());
                verify(commentRepository, never()).save(any(Comment.class));
                verify(postRepository, never()).incrementCommentCount(anyLong());
                verify(commentMapper, never()).toResponse(any(Comment.class));
        }

        @Test
        void getCommentById_ok() {
                Comment comment = new CommentTestBuilder()
                                .withId(1L)
                                .withContent("Hello")
                                .build();

                CommentResponse response = buildCommentResponse(comment);

                when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
                when(commentMapper.toResponse(comment)).thenReturn(response);

                CommentResponse result = commentService.getCommentById(1L);

                assertEquals(1L, result.id());
                assertEquals("Hello", result.content());

                verify(commentRepository).findById(1L);
                verify(commentMapper).toResponse(comment);
        }

        @Test
        void getCommentById_notFound() {
                when(commentRepository.findById(1L)).thenReturn(Optional.empty());

                assertThrows(CommentNotFoundException.class, () -> commentService.getCommentById(1L));

                verify(commentRepository).findById(1L);
                verify(commentMapper, never()).toResponse(any(Comment.class));
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

                CommentResponse response1 = buildCommentResponse(comment1);
                CommentResponse response2 = buildCommentResponse(comment2);

                when(commentRepository.findByAuthorOrderByCreatedAtDesc(author))
                                .thenReturn(List.of(comment1, comment2));
                when(commentMapper.toResponseLevel(eq(comment1), any())).thenReturn(response1);
                when(commentMapper.toResponseLevel(eq(comment2), any())).thenReturn(response2);

                List<CommentResponse> result = commentService.getCommentsByAuthor(author);

                assertEquals(2, result.size());
                assertEquals("Hello", result.get(0).content());
                assertEquals("Bye", result.get(1).content());

                verify(commentRepository).findByAuthorOrderByCreatedAtDesc(author);
                verify(commentMapper).toResponseLevel(eq(comment1), any());
                verify(commentMapper).toResponseLevel(eq(comment2), any());
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

                CommentResponse response1 = buildCommentResponse(comment1);
                CommentResponse response2 = buildCommentResponse(comment2);

                when(commentRepository.findByPostOrderByCreatedAtDesc(post))
                                .thenReturn(List.of(comment1, comment2));
                when(commentMapper.toResponseLevel(eq(comment1), any())).thenReturn(response1);
                when(commentMapper.toResponseLevel(eq(comment2), any())).thenReturn(response2);

                List<CommentResponse> result = commentService.getCommentsByPost(post);

                assertEquals(2, result.size());
                assertEquals("Hello", result.get(0).content());
                assertEquals("Bye", result.get(1).content());

                verify(commentRepository).findByPostOrderByCreatedAtDesc(post);
                verify(commentMapper).toResponseLevel(eq(comment1), any());
                verify(commentMapper).toResponseLevel(eq(comment2), any());
        }

        @Test
        void updateCommentContent_ok() {
                Comment comment = new CommentTestBuilder()
                                .withId(1L)
                                .withContent("Old comment")
                                .build();

                UpdateCommentRequest request = new UpdateCommentRequest("New comment");

                CommentResponse response = buildCommentResponse(
                                new CommentTestBuilder()
                                                .withId(1L)
                                                .withContent("New comment")
                                                .withAuthor(comment.getAuthor())
                                                .withPostConnected(comment.getPost())
                                                .build());

                when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
                when(commentRepository.save(comment)).thenReturn(comment);
                when(commentMapper.toResponse(comment)).thenReturn(response);

                CommentResponse result = commentService.updateCommentContent(1L, request);

                assertEquals("New comment", comment.getContent());
                assertEquals("New comment", result.content());

                verify(commentRepository).findById(1L);
                verify(commentRepository).save(comment);
                verify(commentMapper).toResponse(comment);
        }

        @Test
        void updateCommentContent_emptyContent() {
                UpdateCommentRequest request = new UpdateCommentRequest(" ");

                assertThrows(
                                EmptyCommentContentException.class,
                                () -> commentService.updateCommentContent(1L, request));

                verify(commentRepository, never()).findById(anyLong());
                verify(commentRepository, never()).save(any(Comment.class));
                verify(commentMapper, never()).toResponse(any(Comment.class));
        }

        @Test
        void updateCommentContent_nullContent() {
                UpdateCommentRequest request = new UpdateCommentRequest(null);

                assertThrows(
                                EmptyCommentContentException.class,
                                () -> commentService.updateCommentContent(1L, request));

                verify(commentRepository, never()).findById(anyLong());
                verify(commentRepository, never()).save(any(Comment.class));
                verify(commentMapper, never()).toResponse(any(Comment.class));
        }

        @Test
        void updateCommentContent_notFound() {
                UpdateCommentRequest request = new UpdateCommentRequest("New comment");

                when(commentRepository.findById(1L)).thenReturn(Optional.empty());

                assertThrows(
                                CommentNotFoundException.class,
                                () -> commentService.updateCommentContent(1L, request));

                verify(commentRepository).findById(1L);
                verify(commentRepository, never()).save(any(Comment.class));
                verify(commentMapper, never()).toResponse(any(Comment.class));
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
                                () -> commentService.deleteCommentById(1L));

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
                                () -> commentService.deleteCommentByIdAndAuthor(1L, author));

                verify(commentRepository).findById(1L);
                verify(postRepository, never()).decrementCommentCount(anyLong());
                verify(commentRepository, never()).delete(any(Comment.class));
        }

        @Test
        void deleteCommentByIdAndAuthor_wrongAuthor() {
                User realAuthor = new UserTestBuilder().withId(5L).build();
                User anotherUser = new UserTestBuilder().withId(99L).build();
                Post post = new PostTestBuilder().withId(100L).build();

                Comment comment = new CommentTestBuilder()
                                .withId(1L)
                                .withAuthor(realAuthor)
                                .withPostConnected(post)
                                .build();

                when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

                assertThrows(
                                CommentNotFoundException.class,
                                () -> commentService.deleteCommentByIdAndAuthor(1L, anotherUser));

                verify(commentRepository).findById(1L);
                verify(postRepository, never()).decrementCommentCount(anyLong());
                verify(commentRepository, never()).delete(any(Comment.class));
        }

        private CommentResponse buildCommentResponse(Comment comment) {
                User author = comment.getAuthor() != null
                                ? comment.getAuthor()
                                : new UserTestBuilder()
                                                .withId(100L)
                                                .withVisibleName("Anton")
                                                .withProfileImage("avatar.png")
                                                .build();

                return new CommentResponse(
                                comment.getId(),
                                comment.getContent(),
                                new UserCardResponse(
                                                author.getId(),
                                                author.getVisibleName(),
                                                author.getUsername(),
                                                author.getProfileImage(),
                                                author.getRole(),
                                                false),
                                comment.getCreatedAt() != null ? comment.getCreatedAt() : LocalDateTime.now(),
                                comment.getCreatedAt() != null ? comment.getCreatedAt() : LocalDateTime.now(),
                                null,
                                new java.util.ArrayList<>(),
                                0);
        }

        @Test
        void deleteCommentByIdAndAuthor_Ok() {
                User author = new UserTestBuilder().withId(1L).build();

                Comment comment = new Comment();
                comment.setId(10L);
                comment.setAuthor(author);

                com.autobook.Social.Post.Post post = new com.autobook.Social.Post.Post();
                post.setId(99L);
                comment.setPost(post);

                when(commentRepository.findById(10L)).thenReturn(Optional.of(comment));

                commentService.deleteCommentByIdAndAuthor(10L, author);

                verify(postRepository).decrementCommentCount(99L);
                verify(commentRepository).delete(comment);
        }

        @Test
        void deleteCommentByIdAndAuthor_ThrowsWhenNotAuthor() {
                User author = new UserTestBuilder().withId(1L).build();
                User wrongAuthor = new UserTestBuilder().withId(2L).build();

                Comment comment = new Comment();
                comment.setId(10L);
                comment.setAuthor(author);

                when(commentRepository.findById(10L)).thenReturn(Optional.of(comment));

                assertThrows(CommentNotFoundException.class,
                                () -> commentService.deleteCommentByIdAndAuthor(10L, wrongAuthor));
        }

        @Test
        void incrementLikeCount_Ok() {
                Comment comment = new Comment();
                comment.setId(10L);

                when(commentRepository.findById(10L)).thenReturn(Optional.of(comment));

                commentService.incrementLikeCount(10L);

                verify(commentRepository).incrementLikeCount(10L);
        }
}