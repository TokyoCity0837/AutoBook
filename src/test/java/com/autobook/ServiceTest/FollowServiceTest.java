package com.autobook.ServiceTest;

import com.autobook.Enum.FollowStatus;
import com.autobook.Exception.FollowAlreadyExistsException;
import com.autobook.Exception.FollowNotFoundException;
import com.autobook.Exception.InvalidFollowException;
import com.autobook.Factory.FollowFactory;
import com.autobook.Social.Follow.*;
import com.autobook.Social.Follow.DTO.Response.FollowResponse;
import com.autobook.Social.User.DTO.Response.UserCardResponse;
import com.autobook.Social.User.User;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FollowServiceTest {

    @Mock
    private FollowRepository followRepository;

    @Mock
    private FollowFactory followFactory;

    @Mock
    private FollowMapper followMapper;

    @InjectMocks
    private FollowService followService;

    @Test
    void sendFollowRequest_ok() {
        User follower = new UserTestBuilder().withId(1L).withUsername("anton").build();
        User following = new UserTestBuilder().withId(2L).withUsername("anna").build();

        Follow follow = new Follow();
        follow.setId(10L);
        follow.setFollower(follower);
        follow.setFollowing(following);
        follow.setStatus(FollowStatus.PENDING);

        FollowResponse response = new FollowResponse(
                10L,
                new UserCardResponse(1L, "anton", "anton", null, null),
                new UserCardResponse(2L, "anna", "anna", null, null),
                FollowStatus.PENDING,
                LocalDateTime.now()
        );

        when(followRepository.existsByFollowerAndFollowing(follower, following)).thenReturn(false);
        when(followFactory.create(follower, following, FollowStatus.PENDING)).thenReturn(follow);
        when(followRepository.save(follow)).thenReturn(follow);
        when(followMapper.toResponse(follow)).thenReturn(response);

        FollowResponse result = followService.sendFollowRequest(follower, following);

        assertNotNull(result);
        assertEquals(10L, result.id());
        assertEquals(FollowStatus.PENDING, result.status());

        verify(followRepository).existsByFollowerAndFollowing(follower, following);
        verify(followFactory).create(follower, following, FollowStatus.PENDING);
        verify(followRepository).save(follow);
        verify(followMapper).toResponse(follow);
    }

    @Test
    void sendFollowRequest_shouldThrowWhenAlreadyExists() {
        User follower = new UserTestBuilder().withId(1L).build();
        User following = new UserTestBuilder().withId(2L).build();

        when(followRepository.existsByFollowerAndFollowing(follower, following)).thenReturn(true);

        assertThrows(FollowAlreadyExistsException.class,
                () -> followService.sendFollowRequest(follower, following));

        verify(followRepository).existsByFollowerAndFollowing(follower, following);
        verify(followFactory, never()).create(any(), any(), any());
        verify(followRepository, never()).save(any());
    }

    @Test
    void getFollowById_ok() {
        User follower = new UserTestBuilder().withId(1L).build();
        User following = new UserTestBuilder().withId(2L).build();

        Follow follow = new Follow();
        follow.setId(5L);
        follow.setFollower(follower);
        follow.setFollowing(following);
        follow.setStatus(FollowStatus.PENDING);

        FollowResponse response = new FollowResponse(
                5L,
                new UserCardResponse(1L, "anton", "anton", null, null),
                new UserCardResponse(2L, "anna", "anna", null, null),
                FollowStatus.PENDING,
                LocalDateTime.now()
        );

        when(followRepository.findById(5L)).thenReturn(Optional.of(follow));
        when(followMapper.toResponse(follow)).thenReturn(response);

        FollowResponse result = followService.getFollowById(5L);

        assertEquals(5L, result.id());
        verify(followRepository).findById(5L);
        verify(followMapper).toResponse(follow);
    }

    @Test
    void getFollowById_shouldThrowWhenNotFound() {
        when(followRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(FollowNotFoundException.class,
                () -> followService.getFollowById(1L));
    }

    @Test
    void getPendingRequests_ok() {
        User user = new UserTestBuilder().withId(2L).build();
        Follow follow = new Follow();
        follow.setId(1L);

        FollowResponse response = new FollowResponse(
                1L,
                null,
                null,
                FollowStatus.PENDING,
                LocalDateTime.now()
        );

        when(followRepository.findByFollowingAndStatus(user, FollowStatus.PENDING))
                .thenReturn(List.of(follow));
        when(followMapper.toResponse(follow)).thenReturn(response);

        List<FollowResponse> result = followService.getPendingRequests(user);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).id());
        verify(followMapper).toResponse(follow);
    }

    @Test
    void acceptFollowRequest_ok_whenReverseDoesNotExist() {
        User follower = new UserTestBuilder().withId(1L).build();
        User following = new UserTestBuilder().withId(2L).build();

        Follow request = new Follow();
        request.setId(100L);
        request.setFollower(follower);
        request.setFollowing(following);
        request.setStatus(FollowStatus.PENDING);

        Follow reverseFollow = new Follow();
        reverseFollow.setFollower(following);
        reverseFollow.setFollowing(follower);
        reverseFollow.setStatus(FollowStatus.ACCEPTED);

        FollowResponse response = new FollowResponse(
                100L,
                null,
                null,
                FollowStatus.ACCEPTED,
                LocalDateTime.now()
        );

        when(followRepository.findById(100L)).thenReturn(Optional.of(request));
        when(followRepository.save(request)).thenReturn(request);
        when(followRepository.existsByFollowerAndFollowing(following, follower)).thenReturn(false);
        when(followFactory.create(following, follower, FollowStatus.ACCEPTED)).thenReturn(reverseFollow);
        when(followMapper.toResponse(request)).thenReturn(response);

        FollowResponse result = followService.acceptFollowRequest(100L);

        assertEquals(FollowStatus.ACCEPTED, result.status());
        verify(followRepository).save(request);
        verify(followFactory).create(following, follower, FollowStatus.ACCEPTED);
        verify(followRepository).save(reverseFollow);
        verify(followMapper).toResponse(request);
    }

    @Test
    void rejectFollowRequest_shouldThrowWhenStatusNotPending() {
        Follow request = new Follow();
        request.setId(1L);
        request.setStatus(FollowStatus.ACCEPTED);

        when(followRepository.findById(1L)).thenReturn(Optional.of(request));

        assertThrows(InvalidFollowException.class,
                () -> followService.rejectFollowRequest(1L));

        verify(followRepository, never()).delete(any());
    }

    @Test
    void removeConnection_ok() {
        User firstUser = new UserTestBuilder().withId(1L).build();
        User secondUser = new UserTestBuilder().withId(2L).build();

        Follow firstFollow = new Follow();
        Follow secondFollow = new Follow();

        when(followRepository.findByFollowerAndFollowing(firstUser, secondUser))
                .thenReturn(Optional.of(firstFollow));
        when(followRepository.findByFollowerAndFollowing(secondUser, firstUser))
                .thenReturn(Optional.of(secondFollow));

        followService.removeConnection(firstUser, secondUser);

        verify(followRepository).delete(firstFollow);
        verify(followRepository).delete(secondFollow);
    }
}