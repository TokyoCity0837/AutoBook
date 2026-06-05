package com.autobook.ControllerTest;

import com.autobook.Enum.FollowStatus;
import com.autobook.Social.Follow.DTO.Response.FollowResponse;
import com.autobook.Social.Follow.FollowController;
import com.autobook.Social.Follow.FollowService;
import com.autobook.Social.User.User;
import com.autobook.Social.User.UserRepository;
import com.autobook.util.UserTestBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FollowControllerTest {

    @Mock
    private FollowService followService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Principal principal;

    @InjectMocks
    private FollowController followController;

    private User getMockUser(Long id, String username) {
        return new UserTestBuilder().withId(id).withUsername(username).build();
    }

    @Test
    void getMyFriends() {
        User me = getMockUser(1L, "me");
        when(principal.getName()).thenReturn("me");
        when(userRepository.findByUsername("me")).thenReturn(Optional.of(me));
        when(followService.getFriends(me)).thenReturn(List.of());

        List<FollowResponse> res = followController.getMyFriends(principal);
        assertEquals(0, res.size());
    }

    @Test
    void sendFollowRequest() {
        User me = getMockUser(1L, "me");
        User target = getMockUser(2L, "target");

        when(principal.getName()).thenReturn("me");
        when(userRepository.findByUsername("me")).thenReturn(Optional.of(me));
        when(userRepository.findById(2L)).thenReturn(Optional.of(target));
        
        FollowResponse fr = new FollowResponse(10L, null, null, FollowStatus.PENDING, LocalDateTime.now());
        when(followService.sendFollowRequest(me, target)).thenReturn(fr);

        FollowResponse res = followController.sendFollowRequest(2L, principal);
        assertEquals(10L, res.id());
    }

    @Test
    void getFollowStatus() {
        User me = getMockUser(1L, "me");
        User target = getMockUser(2L, "target");

        when(principal.getName()).thenReturn("me");
        when(userRepository.findByUsername("me")).thenReturn(Optional.of(me));
        when(userRepository.findById(2L)).thenReturn(Optional.of(target));
        when(followService.isFollowing(me, target)).thenReturn(true);

        boolean stat = followController.getFollowStatus(2L, principal);
        assertTrue(stat);
    }
    
    @Test
    void acceptFollowRequest() {
        FollowResponse fr = new FollowResponse(10L, null, null, FollowStatus.ACCEPTED, LocalDateTime.now());
        when(followService.acceptFollowRequest(10L)).thenReturn(fr);

        FollowResponse res = followController.acceptFollowRequest(10L);
        assertEquals(FollowStatus.ACCEPTED, res.status());
    }

    @Test
    void removeConnection() {
        User me = getMockUser(1L, "me");
        User target = getMockUser(2L, "target");

        when(principal.getName()).thenReturn("me");
        when(userRepository.findByUsername("me")).thenReturn(Optional.of(me));
        when(userRepository.findById(2L)).thenReturn(Optional.of(target));

        followController.removeConnection(2L, principal);
        verify(followService).removeConnection(me, target);
    }
}
