package com.autobook.ServiceTest;

import com.autobook.Exception.PostNotFoundException;
import com.autobook.Factory.PostFactory;
import com.autobook.Social.Post.*;
import com.autobook.Social.Post.PostLikes.PostLike;
import com.autobook.Social.Post.PostLikes.PostLikeId;
import com.autobook.Social.Post.PostLikes.PostLikeRepository;
import com.autobook.Social.Post.PostReposts.PostRepostRepository;
import com.autobook.Social.User.User;
import com.autobook.util.PostTestBuilder;
import com.autobook.util.UserTestBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Additional PostService tests — focussed on toggleLike and
 * incrementRepostCount
 * which are absent from the main PostServiceTest file.
 */
@ExtendWith(MockitoExtension.class)
class PostServiceExtendedTest {

    @Mock
    private PostRepository postRepository;
    @Mock
    private PostFactory postFactory;
    @Mock
    private PostMapper postMapper;
    @Mock
    private PostLikeRepository postLikeRepository;
    @Mock
    private PostRepostRepository postRepostRepository;

    @InjectMocks
    private PostService postService;

    // toggleLike

    @Test
    void toggleLike_addsLike_whenNotLiked() {
        User user = new UserTestBuilder().withId(1L).build();
        Post post = new PostTestBuilder().withId(10L).build();

        PostLikeId id = new PostLikeId(user.getId(), 10L);
        when(postRepository.findById(10L)).thenReturn(Optional.of(post));
        when(postLikeRepository.existsById(id)).thenReturn(false);

        boolean result = postService.toggleLike(10L, user);

        assertTrue(result);
        verify(postLikeRepository).save(any(PostLike.class));
        verify(postRepository).incrementLikeCount(10L);
    }

    @Test
    void toggleLike_removesLike_whenAlreadyLiked() {
        User user = new UserTestBuilder().withId(1L).build();
        Post post = new PostTestBuilder().withId(10L).build();

        PostLikeId id = new PostLikeId(user.getId(), 10L);
        when(postRepository.findById(10L)).thenReturn(Optional.of(post));
        when(postLikeRepository.existsById(id)).thenReturn(true);

        boolean result = postService.toggleLike(10L, user);

        assertFalse(result);
        verify(postLikeRepository).deleteById(id);
        verify(postRepository).decrementLikeCount(10L);
    }

    @Test
    void toggleLike_throwsWhenPostNotFound() {
        User user = new UserTestBuilder().withId(1L).build();
        when(postRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(PostNotFoundException.class, () -> postService.toggleLike(99L, user));
        verify(postLikeRepository, never()).save(any());
        verify(postLikeRepository, never()).deleteById(any());
    }

    // incrementRepostCount

    @Test
    void incrementRepostCount_ok() {
        Post post = new PostTestBuilder().withId(5L).build();
        when(postRepository.findById(5L)).thenReturn(Optional.of(post));

        postService.incrementRepostCount(5L);

        verify(postRepository).incrementRepostCount(5L);
    }

    @Test
    void incrementRepostCount_throwsWhenPostNotFound() {
        when(postRepository.findById(5L)).thenReturn(Optional.empty());
        assertThrows(PostNotFoundException.class, () -> postService.incrementRepostCount(5L));
        verify(postRepository, never()).incrementRepostCount(any());
    }
}
