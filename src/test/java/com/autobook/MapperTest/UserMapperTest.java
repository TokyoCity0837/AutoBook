package com.autobook.MapperTest;

import com.autobook.Enum.UserRole;
import com.autobook.Social.Follow.FollowService;
import com.autobook.Social.User.DTO.Response.UserCardResponse;
import com.autobook.Social.User.DTO.Response.UserPostDetailsResponse;
import com.autobook.Social.User.DTO.Response.UserProfileResponse;
import com.autobook.Social.User.User;
import com.autobook.Social.User.UserMapper;
import com.autobook.Social.User.UserRepository;
import com.autobook.util.UserTestBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {

    @Mock
    private FollowService followService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserMapper userMapper;

    @Test
    void toProfileResponse() {
        User user = new UserTestBuilder().withId(1L).withUsername("testuser").withRole(UserRole.USER).build();
        user.setVisibleName("Test User");
        user.setBio("My bio");

        UserProfileResponse response = userMapper.toProfileResponse(
                user,
                Collections.emptyList(),
                Collections.emptyList(),
                5L,
                2L,
                false,
                false);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("testuser", response.username());
        assertEquals("Test User", response.visibleName());
        assertEquals("My bio", response.bio());
        assertEquals(5L, response.followers());
        assertEquals(2L, response.friends());
    }

    @Test
    void toCardResponse_whenNotAuthenticated() {
        User user = new UserTestBuilder().withId(1L).withUsername("testuser").withRole(UserRole.USER).build();

        SecurityContext context = mock(SecurityContext.class);
        Authentication auth = mock(Authentication.class);
        when(context.getAuthentication()).thenReturn(auth);
        when(auth.isAuthenticated()).thenReturn(false);
        SecurityContextHolder.setContext(context);

        UserCardResponse response = userMapper.toCardResponse(user);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals(false, response.isFriend());
    }

    @Test
    void toPostDetailsResponse() {
        User user = new UserTestBuilder().withId(1L).withUsername("testuser").withRole(UserRole.USER).build();
        user.setVisibleName("Test User");
        user.setBio("My bio");

        UserPostDetailsResponse response = userMapper.toPostDetailsResponse(user);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Test User", response.visibleName());
    }
}
