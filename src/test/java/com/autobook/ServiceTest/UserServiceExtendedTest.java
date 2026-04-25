package com.autobook.ServiceTest;

import com.autobook.Enum.PrivacyType;
import com.autobook.Enum.UserRole;
import com.autobook.Social.Follow.FollowService;
import com.autobook.Social.Post.PostMapper;
import com.autobook.Social.Post.PostRepository;
import com.autobook.Social.Post.PostLikes.PostLikeRepository;
import com.autobook.Social.Post.PostReposts.PostRepostRepository;
import com.autobook.Social.User.*;
import com.autobook.Social.User.DTO.Request.UserUpdateRequest;
import com.autobook.Social.User.DTO.Response.UserCardResponse;
import com.autobook.Social.User.DTO.Response.UserProfileResponse;
import com.autobook.util.UserTestBuilder;
import com.autobook.Library.Book.BookRepository;
import com.autobook.Library.Book.BookMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceExtendedTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private BookMapper bookMapper;
    @Mock
    private PostRepository postRepository;
    @Mock
    private PostLikeRepository postLikeRepository;
    @Mock
    private PostRepostRepository postRepostRepository;
    @Mock
    private PostMapper postMapper;
    @Mock
    private FollowService followService;

    @InjectMocks
    private UserService userService;

    // ─── Simple Gets ─────────────────────────────────────────────────────────

    @Test
    void getAllUsers_ReturnsList() {
        User user = new UserTestBuilder().withId(1L).build();
        UserCardResponse card = new UserCardResponse(1L, "Visible", "username", null, UserRole.USER, false);

        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toCardResponse(user)).thenReturn(card);

        List<UserCardResponse> res = userService.getAllUsers();
        assertEquals(1, res.size());
        assertEquals("username", res.get(0).username());
    }

    @Test
    void getUsersByRole_ReturnsList() {
        User user = new UserTestBuilder().withId(1L).withRole(UserRole.ADMIN).build();
        UserCardResponse card = new UserCardResponse(1L, "Visible", "admin", null, UserRole.ADMIN, false);

        when(userRepository.findByRole(UserRole.ADMIN)).thenReturn(List.of(user));
        when(userMapper.toCardResponse(user)).thenReturn(card);

        List<UserCardResponse> res = userService.getUsersByRole(UserRole.ADMIN);
        assertEquals(1, res.size());
    }

    @Test
    void searchUsersByUsername_ReturnsList() {
        User user = new UserTestBuilder().withId(1L).build();
        when(userRepository.findByUsernameContainingIgnoreCase("t")).thenReturn(List.of(user));
        when(userMapper.toCardResponse(user)).thenReturn(null);

        assertEquals(1, userService.searchUsersByUsername("t").size());
    }

    @Test
    void getUsersByPrivacy_ReturnsList() {
        User user = new UserTestBuilder().withId(1L).build();
        when(userRepository.findByPrivacy(PrivacyType.PUBLIC)).thenReturn(List.of(user));
        when(userMapper.toCardResponse(user)).thenReturn(null);

        assertEquals(1, userService.getUsersByPrivacy(PrivacyType.PUBLIC).size());
    }

    @Test
    void countUsersByPrivacy_ReturnsCount() {
        when(userRepository.countByPrivacy(PrivacyType.PRIVATE)).thenReturn(10L);
        assertEquals(10L, userService.countUsersByPrivacy(PrivacyType.PRIVATE));
    }

    @Test
    void existsByEmail_ReturnsTrue() {
        when(userRepository.existsByEmail("test@test.com")).thenReturn(true);
        assertTrue(userService.existsByEmail("test@test.com"));
    }

    @Test
    void existsByUsername_ReturnsTrue() {
        when(userRepository.existsByUsername("test")).thenReturn(true);
        assertTrue(userService.existsByUsername("test"));
    }

    // ─── Update Profile ──────────────────────────────────────────────────────

    @Test
    void updateProfile_Ok() {
        User user = new UserTestBuilder().withId(1L).withUsername("old").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserUpdateRequest req = new UserUpdateRequest("New Name", "Bio", "img", PrivacyType.PRIVATE, "new_username");

        when(userRepository.existsByUsername("new_username")).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);

        // Security stub
        SecurityContext ctx = mock(SecurityContext.class);
        Authentication auth = mock(Authentication.class);
        when(ctx.getAuthentication()).thenReturn(auth);
        when(auth.isAuthenticated()).thenReturn(false);
        SecurityContextHolder.setContext(ctx);

        when(userMapper.toProfileResponse(any(), any(), any(), anyLong(), anyLong(), anyBoolean(), anyBoolean()))
                .thenReturn(
                        new UserProfileResponse(1L, "new_username", "New Name", "Bio", "img", PrivacyType.PRIVATE, null,
                                UserRole.USER, 0, 0, null, null, false, false));

        UserProfileResponse res = userService.updateProfile(1L, req);
        assertEquals("new_username", res.username());
    }

    @Test
    void deleteUserById_Ok() {
        User user = new UserTestBuilder().withId(1L).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUserById(1L);

        verify(userRepository).delete(user);
    }
}
