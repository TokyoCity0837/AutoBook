package com.autobook.ServiceTest;

import org.junit.jupiter.api.extension.ExtendWith;
import com.autobook.Social.User.User;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;

import com.autobook.Enum.PostType;
import com.autobook.Exception.EmptyPostContentException;
import com.autobook.Exception.PostNotFoundException;
import com.autobook.Social.Post.*;
import com.autobook.Factory.PostFactory;
import com.autobook.util.PostTestBuilder;
import com.autobook.util.UserTestBuilder;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostFactory postFactory;

    @InjectMocks
    private PostService postService;

    @Test
    void createPost_ok(){
        User author = new UserTestBuilder().build();

        Post post = new PostTestBuilder()
            .withContent("Hello bro")
            .withAuthor(author)
            .withPostType(PostType.FEED)
            .build();
        when(postFactory.create("Hello bro", author, PostType.FEED)).thenReturn(post);

        when(postRepository.save(post)).thenReturn(post);
        Post result = postService.createPost("Hello bro", author, PostType.FEED);

        assertNotNull(result);
        assertEquals("Hello bro", result.getContent());
        assertEquals(author, result.getAuthor());
        assertEquals(PostType.FEED, result.getPostType());

        verify(postFactory).create("Hello bro", author, PostType.FEED);
        verify(postRepository).save(post);
    }


    @Test
    void createPost_emptyContent(){
        User author = new UserTestBuilder().build();

        assertThrows(
            EmptyPostContentException.class, 
            () -> postService.createPost(" ", author, PostType.FEED)
        );

        verify(postFactory, never()).create(any(), any(), any());
        verify(postRepository, never()).save(any(Post.class));

    }

    @Test
    void getPostById_ok(){
        Post post = new PostTestBuilder()
            .withId(1L)
            .withContent("Hello guys")
            .build();

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        Post result = postService.getPostById(1L);

        assertEquals(1L, result.getId());
        assertEquals("Hello guys", result.getContent());

        verify(postRepository).findById(1L);
    }
    
    @Test
    void getPostById_notFound(){
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(PostNotFoundException.class, () -> postService.getPostById(1L));
    }

    @Test
    void getAllPosts_ok(){
        Post post1 = new PostTestBuilder().withId(1L).withContent("Hi!").build();
        Post post2 = new PostTestBuilder().withId(2L).withContent("Bye!").build();

        when(postRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(post1, post2));

        List<Post> result = postService.getAllPosts();

        assertEquals(2, result.size());
        assertEquals("Hi!", result.get(0).getContent());
        assertEquals("Bye!", result.get(1).getContent());

        verify(postRepository).findAllByOrderByCreatedAtDesc();
    }

    @Test
    void getFeedPosts_ok(){
        Post post1 = new PostTestBuilder().withId(1L).withContent("Hi!").withPostType(PostType.FEED).build();
        Post post2 = new PostTestBuilder().withId(2L).withContent("Bye!").withPostType(PostType.FEED).build();

        when(postRepository.findByPostTypeOrderByCreatedAtDesc(PostType.FEED)).thenReturn(List.of(post1, post2));

        List<Post> result = postService.getFeedPosts();

        assertEquals(2, result.size());
        assertEquals("Hi!", result.get(0).getContent());
        assertEquals("Bye!", result.get(1).getContent());

        verify(postRepository).findByPostTypeOrderByCreatedAtDesc(PostType.FEED);
    }

    @Test
    void getProfilePosts_ok(){
        User author = new UserTestBuilder().build();

        Post post1 = new PostTestBuilder().withId(1L).withContent("Hi!").withAuthor(author).withPostType(PostType.PROFILE).build();
        Post post2 = new PostTestBuilder().withId(2L).withContent("Bye!").withAuthor(author).withPostType(PostType.PROFILE).build();

        when(postRepository.findByAuthorAndPostTypeOrderByCreatedAtDesc(author, PostType.PROFILE)).thenReturn(List.of(post1, post2));

        List<Post> result = postService.getProfilePosts(author);

        assertEquals(2, result.size());
        assertEquals("Hi!", result.get(0).getContent());
        assertEquals("Bye!", result.get(1).getContent());

        verify(postRepository).findByAuthorAndPostTypeOrderByCreatedAtDesc(author, PostType.PROFILE);
    }

    @Test
    void getPostsByAuthor_ok(){
        User author = new UserTestBuilder().build();

        Post post1 = new PostTestBuilder().withId(1L).withContent("Hi!").withAuthor(author).build();
        Post post2 = new PostTestBuilder().withId(2L).withContent("Bye!").withAuthor(author).build();

        when(postRepository.findByAuthorOrderByCreatedAtDesc(author)).thenReturn(List.of(post1, post2));

        List<Post> result = postService.getPostsByAuthor(author);

        assertEquals(2, result.size());
        assertEquals("Hi!", result.get(0).getContent());
        assertEquals("Bye!", result.get(1).getContent());

        verify(postRepository).findByAuthorOrderByCreatedAtDesc(author);
    }

    @Test
    void getFeedPostsByAuthors_ok(){
        User author1 = new UserTestBuilder().withId(1L).withUsername("anton1").build();
        User author2 = new UserTestBuilder().withId(2L).withUsername("anton2").build();

        List<User> authors = List.of(author1, author2);


        Post post1 = new PostTestBuilder().withId(1L).withContent("Hi!").withPostType(PostType.FEED).withAuthor(author1).build();
        Post post2 = new PostTestBuilder().withId(2L).withContent("Bye!").withPostType(PostType.FEED).withAuthor(author1).build();
        Post post3 = new PostTestBuilder().withId(3L).withContent("Hi-Bye!!").withPostType(PostType.FEED).withAuthor(author2).build();

        when(postRepository.findByAuthorInAndPostTypeOrderByCreatedAtDesc(authors, PostType.FEED)).thenReturn(List.of(post1, post2, post3));

        List<Post> result = postService.getFeedPostsByAuthors(authors);

        assertEquals(3, result.size());
        assertEquals("Hi!", result.get(0).getContent());
        assertEquals(2L, result.get(1).getId());
        assertEquals(author2, result.get(2).getAuthor());

        verify(postRepository).findByAuthorInAndPostTypeOrderByCreatedAtDesc(authors, PostType.FEED);
    }

    @Test
    void countProfilePostsByAuthor_ok(){
        User author = new UserTestBuilder().build();

        when(postRepository.countByAuthorAndPostType(author, PostType.PROFILE)).thenReturn(2L);
        Long result = postService.countProfilePostsByAuthor(author);

        assertEquals(2L, result);

        verify(postRepository).countByAuthorAndPostType(author, PostType.PROFILE);
    }

    @Test
    void updatePostContent_ok() {
        Post post = new PostTestBuilder()
            .withId(1L)
            .withContent("Old content")
            .build();

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(postRepository.save(post)).thenReturn(post);

        Post result = postService.updatePostContent(1L, "New content");

        assertEquals("New content", result.getContent());

        verify(postRepository).findById(1L);
        verify(postRepository).save(post);

    }

    @Test
    void updatePostContent_emptyContent() {
        assertThrows(
            EmptyPostContentException.class,
            () -> postService.updatePostContent(1L, " ")
        );

        verify(postRepository, never()).findById(anyLong());
        verify(postRepository, never()).save(any(Post.class));

    }

    @Test
    void updatePostContent_postNotFound() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
            PostNotFoundException.class,
            () -> postService.updatePostContent(1L, "New content")
        );

        verify(postRepository).findById(1L);
        verify(postRepository, never()).save(any(Post.class));
        
    }

    @Test
    void deletePost_ok() {
        Post post = new PostTestBuilder()
            .withId(1L)
            .build();

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        postService.deletePost(1L);

        verify(postRepository).findById(1L);
        verify(postRepository).delete(post);

    }

    @Test
    void deletePost_notFound() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
            PostNotFoundException.class,
            () -> postService.deletePost(1L)
        );

        verify(postRepository).findById(1L);
        verify(postRepository, never()).delete(any(Post.class));

    }

    @Test
    void incrementLikeCount_ok() {
        Post post = new PostTestBuilder().withId(1L).build();

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        postService.incrementLikeCount(1L);

        verify(postRepository).findById(1L);
        verify(postRepository).incrementLikeCount(1L);

    }

    @Test
    void incrementLikeCount_notFound() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
            PostNotFoundException.class,
            () -> postService.incrementLikeCount(1L)
        );

        verify(postRepository).findById(1L);
        verify(postRepository, never()).incrementLikeCount(anyLong());

    }

    @Test
    void decrementLikeCount_ok() {
        Post post = new PostTestBuilder().withId(1L).build();

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        postService.decrementLikeCount(1L);

        verify(postRepository).findById(1L);
        verify(postRepository).decrementLikeCount(1L);

    }

    @Test
    void decrementLikeCount_notFound() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
            PostNotFoundException.class,
            () -> postService.decrementLikeCount(1L)
        );

    verify(postRepository).findById(1L);
    verify(postRepository, never()).decrementLikeCount(anyLong());
    }

    @Test
    void incrementCommentCount_ok() {
        Post post = new PostTestBuilder().withId(1L).build();

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        postService.incrementCommentCount(1L);

        verify(postRepository).findById(1L);
        verify(postRepository).incrementCommentCount(1L);
    }

    @Test
    void incrementCommentCount_notFound() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
            PostNotFoundException.class,
            () -> postService.incrementCommentCount(1L)
        );

        verify(postRepository).findById(1L);
        verify(postRepository, never()).incrementCommentCount(anyLong());
    }

    @Test
    void decrementCommentCount_ok() {
        Post post = new PostTestBuilder().withId(1L).build();

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        postService.decrementCommentCount(1L);

        verify(postRepository).findById(1L);
        verify(postRepository).decrementCommentCount(1L);
    }

    @Test
    void decrementCommentCount_notFound() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
            PostNotFoundException.class,
            () -> postService.decrementCommentCount(1L)
        );

        verify(postRepository).findById(1L);
        verify(postRepository, never()).decrementCommentCount(anyLong());

    }

    @Test
    void incrementRepostCount_ok() {
        Post post = new PostTestBuilder().withId(1L).build();

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        postService.incrementRepostCount(1L);

        verify(postRepository).findById(1L);
        verify(postRepository).incrementRepostCount(1L);

    }

    @Test
    void incrementRepostCount_notFound() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
            PostNotFoundException.class,
            () -> postService.incrementRepostCount(1L)
        );

        verify(postRepository).findById(1L);
        verify(postRepository, never()).incrementRepostCount(anyLong());

    }

}
