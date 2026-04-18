package com.autobook.ServiceTest;

import com.autobook.Enum.PostType;
import com.autobook.Exception.EmptyPostContentException;
import com.autobook.Exception.PostNotFoundException;
import com.autobook.Factory.PostFactory;
import com.autobook.Social.Comment.DTO.Response.CommentResponse;
import com.autobook.Social.Post.DTO.Request.CreatePostRequest;
import com.autobook.Social.Post.DTO.Request.UpdatePostRequest;
import com.autobook.Social.Post.DTO.Response.PostDetailsResponse;
import com.autobook.Social.Post.DTO.Response.PostResponse;
import com.autobook.Social.Post.Post;
import com.autobook.Social.Post.PostMapper;
import com.autobook.Social.Post.PostRepository;
import com.autobook.Social.Post.PostService;
import com.autobook.Social.User.DTO.Response.UserCardResponse;
import com.autobook.Social.User.DTO.Response.UserPostDetailsResponse;
import com.autobook.Social.User.User;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostFactory postFactory;

    @Mock
    private PostMapper postMapper;

    @InjectMocks
    private PostService postService;

    @Test
    void createPost_ok() {
        User author = new UserTestBuilder()
                .withId(1L)
                .withVisibleName("Anton")
                .withBio("Java dev")
                .build();

        CreatePostRequest request = new CreatePostRequest("Hello bro", PostType.FEED);

        Post post = new PostTestBuilder()
                .withId(1L)
                .withContent("Hello bro")
                .withAuthor(author)
                .withPostType(PostType.FEED)
                .build();

        PostDetailsResponse response = buildPostDetailsResponse(post);

        when(postFactory.create("Hello bro", author, PostType.FEED)).thenReturn(post);
        when(postRepository.save(post)).thenReturn(post);
        when(postMapper.toDetailsResponse(post)).thenReturn(response);

        PostDetailsResponse result = postService.createPost(request, author);

        assertNotNull(result);
        assertEquals("Hello bro", result.content());
        assertEquals(PostType.FEED, result.postType());
        assertEquals("Anton", result.author().visibleName());

        verify(postFactory).create("Hello bro", author, PostType.FEED);
        verify(postRepository).save(post);
        verify(postMapper).toDetailsResponse(post);
    }

    @Test
    void createPost_emptyContent() {
        User author = new UserTestBuilder().build();
        CreatePostRequest request = new CreatePostRequest(" ", PostType.FEED);

        assertThrows(
                EmptyPostContentException.class,
                () -> postService.createPost(request, author)
        );

        verify(postFactory, never()).create(any(), any(), any());
        verify(postRepository, never()).save(any(Post.class));
        verify(postMapper, never()).toDetailsResponse(any(Post.class));
    }

    @Test
    void getPostById_ok() {
        Post post = new PostTestBuilder()
                .withId(1L)
                .withContent("Hello guys")
                .build();

        PostDetailsResponse response = buildPostDetailsResponse(post);

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(postMapper.toDetailsResponse(post)).thenReturn(response);

        PostDetailsResponse result = postService.getPostById(1L);

        assertEquals(1L, result.id());
        assertEquals("Hello guys", result.content());

        verify(postRepository).findById(1L);
        verify(postMapper).toDetailsResponse(post);
    }

    @Test
    void getPostById_notFound() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(PostNotFoundException.class, () -> postService.getPostById(1L));

        verify(postRepository).findById(1L);
        verify(postMapper, never()).toDetailsResponse(any(Post.class));
    }

    @Test
    void getAllPosts_ok() {
        Post post1 = new PostTestBuilder().withId(1L).withContent("Hi!").build();
        Post post2 = new PostTestBuilder().withId(2L).withContent("Bye!").build();

        PostResponse response1 = buildPostResponse(post1);
        PostResponse response2 = buildPostResponse(post2);

        when(postRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(post1, post2));
        when(postMapper.toResponse(post1)).thenReturn(response1);
        when(postMapper.toResponse(post2)).thenReturn(response2);

        List<PostResponse> result = postService.getAllPosts();

        assertEquals(2, result.size());
        assertEquals("Hi!", result.get(0).content());
        assertEquals("Bye!", result.get(1).content());

        verify(postRepository).findAllByOrderByCreatedAtDesc();
        verify(postMapper).toResponse(post1);
        verify(postMapper).toResponse(post2);
    }

    @Test
    void getFeedPosts_ok() {
        Post post1 = new PostTestBuilder().withId(1L).withContent("Hi!").withPostType(PostType.FEED).build();
        Post post2 = new PostTestBuilder().withId(2L).withContent("Bye!").withPostType(PostType.FEED).build();

        PostResponse response1 = buildPostResponse(post1);
        PostResponse response2 = buildPostResponse(post2);

        when(postRepository.findByPostTypeOrderByCreatedAtDesc(PostType.FEED)).thenReturn(List.of(post1, post2));
        when(postMapper.toResponse(post1)).thenReturn(response1);
        when(postMapper.toResponse(post2)).thenReturn(response2);

        List<PostResponse> result = postService.getFeedPosts();

        assertEquals(2, result.size());
        assertEquals("Hi!", result.get(0).content());
        assertEquals("Bye!", result.get(1).content());

        verify(postRepository).findByPostTypeOrderByCreatedAtDesc(PostType.FEED);
        verify(postMapper).toResponse(post1);
        verify(postMapper).toResponse(post2);
    }

    @Test
    void getProfilePosts_ok() {
        User author = new UserTestBuilder().build();

        Post post1 = new PostTestBuilder().withId(1L).withContent("Hi!").withAuthor(author).withPostType(PostType.PROFILE).build();
        Post post2 = new PostTestBuilder().withId(2L).withContent("Bye!").withAuthor(author).withPostType(PostType.PROFILE).build();

        PostResponse response1 = buildPostResponse(post1);
        PostResponse response2 = buildPostResponse(post2);

        when(postRepository.findByAuthorAndPostTypeOrderByCreatedAtDesc(author, PostType.PROFILE)).thenReturn(List.of(post1, post2));
        when(postMapper.toResponse(post1)).thenReturn(response1);
        when(postMapper.toResponse(post2)).thenReturn(response2);

        List<PostResponse> result = postService.getProfilePosts(author);

        assertEquals(2, result.size());
        assertEquals("Hi!", result.get(0).content());
        assertEquals("Bye!", result.get(1).content());

        verify(postRepository).findByAuthorAndPostTypeOrderByCreatedAtDesc(author, PostType.PROFILE);
        verify(postMapper).toResponse(post1);
        verify(postMapper).toResponse(post2);
    }

    @Test
    void getPostsByAuthor_ok() {
        User author = new UserTestBuilder().build();

        Post post1 = new PostTestBuilder().withId(1L).withContent("Hi!").withAuthor(author).build();
        Post post2 = new PostTestBuilder().withId(2L).withContent("Bye!").withAuthor(author).build();

        PostResponse response1 = buildPostResponse(post1);
        PostResponse response2 = buildPostResponse(post2);

        when(postRepository.findByAuthorOrderByCreatedAtDesc(author)).thenReturn(List.of(post1, post2));
        when(postMapper.toResponse(post1)).thenReturn(response1);
        when(postMapper.toResponse(post2)).thenReturn(response2);

        List<PostResponse> result = postService.getPostsByAuthor(author);

        assertEquals(2, result.size());
        assertEquals("Hi!", result.get(0).content());
        assertEquals("Bye!", result.get(1).content());

        verify(postRepository).findByAuthorOrderByCreatedAtDesc(author);
        verify(postMapper).toResponse(post1);
        verify(postMapper).toResponse(post2);
    }

    @Test
    void getFeedPostsByAuthors_ok() {
        User author1 = new UserTestBuilder().withId(1L).withUsername("anton1").withVisibleName("Anton 1").build();
        User author2 = new UserTestBuilder().withId(2L).withUsername("anton2").withVisibleName("Anton 2").build();

        List<User> authors = List.of(author1, author2);

        Post post1 = new PostTestBuilder().withId(1L).withContent("Hi!").withPostType(PostType.FEED).withAuthor(author1).build();
        Post post2 = new PostTestBuilder().withId(2L).withContent("Bye!").withPostType(PostType.FEED).withAuthor(author1).build();
        Post post3 = new PostTestBuilder().withId(3L).withContent("Hi-Bye!!").withPostType(PostType.FEED).withAuthor(author2).build();

        PostResponse response1 = buildPostResponse(post1);
        PostResponse response2 = buildPostResponse(post2);
        PostResponse response3 = buildPostResponse(post3);

        when(postRepository.findByAuthorInAndPostTypeOrderByCreatedAtDesc(authors, PostType.FEED))
                .thenReturn(List.of(post1, post2, post3));
        when(postMapper.toResponse(post1)).thenReturn(response1);
        when(postMapper.toResponse(post2)).thenReturn(response2);
        when(postMapper.toResponse(post3)).thenReturn(response3);

        List<PostResponse> result = postService.getFeedPostsByAuthors(authors);

        assertEquals(3, result.size());
        assertEquals("Hi!", result.get(0).content());
        assertEquals(2L, result.get(1).id());
        assertEquals("Anton 2", result.get(2).author().visibleName());

        verify(postRepository).findByAuthorInAndPostTypeOrderByCreatedAtDesc(authors, PostType.FEED);
        verify(postMapper).toResponse(post1);
        verify(postMapper).toResponse(post2);
        verify(postMapper).toResponse(post3);
    }

    @Test
    void countProfilePostsByAuthor_ok() {
        User author = new UserTestBuilder().build();

        when(postRepository.countByAuthorAndPostType(author, PostType.PROFILE)).thenReturn(2L);

        Long result = postService.countProfilePostsByAuthor(author);

        assertEquals(2L, result);

        verify(postRepository).countByAuthorAndPostType(author, PostType.PROFILE);
    }

    @Test
    void updatePostContent_ok() {
        UpdatePostRequest request = new UpdatePostRequest("New content");

        Post post = new PostTestBuilder()
                .withId(1L)
                .withContent("Old content")
                .build();

        PostDetailsResponse response = buildPostDetailsResponse(
                new PostTestBuilder()
                        .withId(1L)
                        .withContent("New content")
                        .withAuthor(post.getAuthor())
                        .withPostType(post.getPostType())
                        .build()
        );

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(postRepository.save(post)).thenReturn(post);
        when(postMapper.toDetailsResponse(post)).thenReturn(response);

        PostDetailsResponse result = postService.updatePostContent(1L, request);

        assertEquals("New content", post.getContent());
        assertEquals("New content", result.content());

        verify(postRepository).findById(1L);
        verify(postRepository).save(post);
        verify(postMapper).toDetailsResponse(post);
    }

    @Test
    void updatePostContent_emptyContent() {
        UpdatePostRequest request = new UpdatePostRequest(" ");

        assertThrows(
                EmptyPostContentException.class,
                () -> postService.updatePostContent(1L, request)
        );

        verify(postRepository, never()).findById(anyLong());
        verify(postRepository, never()).save(any(Post.class));
        verify(postMapper, never()).toDetailsResponse(any(Post.class));
    }

    @Test
    void updatePostContent_postNotFound() {
        UpdatePostRequest request = new UpdatePostRequest("New content");

        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
                PostNotFoundException.class,
                () -> postService.updatePostContent(1L, request)
        );

        verify(postRepository).findById(1L);
        verify(postRepository, never()).save(any(Post.class));
        verify(postMapper, never()).toDetailsResponse(any(Post.class));
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

    private PostResponse buildPostResponse(Post post) {
        User author = post.getAuthor() != null
                ? post.getAuthor()
                : new UserTestBuilder()
                        .withId(100L)
                        .withVisibleName("Anton")
                        .withProfileImage("avatar.png")
                        .build();

        return new PostResponse(
                post.getId(),
                post.getContent(),
                new UserCardResponse(
                        author.getId(),
                        author.getVisibleName(),
                        author.getProfileImage(),
                        author.getRole()
                ),
                post.getPostType(),
                post.getCreatedAt() != null ? post.getCreatedAt() : LocalDateTime.now(),
                post.getUpdatedAt() != null ? post.getUpdatedAt() : LocalDateTime.now(),
                post.getLikeCount(),
                post.getCommentCount(),
                post.getRepostCount()
        );
    }

    private PostDetailsResponse buildPostDetailsResponse(Post post) {
        User author = post.getAuthor() != null
                ? post.getAuthor()
                : new UserTestBuilder()
                        .withId(100L)
                        .withVisibleName("Anton")
                        .withProfileImage("avatar.png")
                        .withBio("Default bio")
                        .build();

        return new PostDetailsResponse(
                post.getId(),
                post.getContent(),
                new UserPostDetailsResponse(
                        author.getId(),
                        author.getVisibleName(),
                        author.getProfileImage(),
                        author.getBio(),
                        author.getRole()
                ),
                post.getPostType(),
                post.getCreatedAt() != null ? post.getCreatedAt() : LocalDateTime.now(),
                post.getUpdatedAt() != null ? post.getUpdatedAt() : LocalDateTime.now(),
                post.getLikeCount(),
                post.getCommentCount(),
                post.getRepostCount(),
                List.of(
                        new CommentResponse(
                                1L,
                                "Nice post",
                                new UserCardResponse(
                                        author.getId(),
                                        author.getVisibleName(),
                                        author.getProfileImage(),
                                        author.getRole()
                                ),
                                LocalDateTime.now()
                        )
                )
        );
    }
}