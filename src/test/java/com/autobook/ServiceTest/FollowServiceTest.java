package com.autobook.ServiceTest;

import com.autobook.Enum.FollowStatus;
import com.autobook.Exception.FollowAlreadyExistsException;
import com.autobook.Exception.FollowNotFoundException;
import com.autobook.Exception.InvalidFollowException;
import com.autobook.Factory.FollowFactory;
import com.autobook.Social.Follow.Follow;
import com.autobook.Social.Follow.FollowRepository;
import com.autobook.Social.Follow.FollowService;
import com.autobook.Social.User.User;
import com.autobook.util.FollowTestBuilder;
import com.autobook.util.UserTestBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FollowServiceTest {

    @Mock
    private FollowRepository followRepository;

    @Mock
    private FollowFactory followFactory;

    @InjectMocks
    private FollowService followService;

    @Test
    void sendFollowRequest_ok() {
        User follower = new UserTestBuilder().withId(1L).build();
        User following = new UserTestBuilder().withId(2L).build();

        Follow follow = new FollowTestBuilder()
                .withFollower(follower)
                .withFollowing(following)
                .withStatus(FollowStatus.PENDING)
                .build();

        when(followRepository.existsByFollowerAndFollowing(follower, following)).thenReturn(false);
        when(followFactory.create(follower, following, FollowStatus.PENDING)).thenReturn(follow);
        when(followRepository.save(follow)).thenReturn(follow);

        Follow result = followService.sendFollowRequest(follower, following);

        assertNotNull(result);
        assertEquals(follower, result.getFollower());
        assertEquals(following, result.getFollowing());
        assertEquals(FollowStatus.PENDING, result.getStatus());

        verify(followRepository).existsByFollowerAndFollowing(follower, following);
        verify(followFactory).create(follower, following, FollowStatus.PENDING);
        verify(followRepository).save(follow);
    }

    @Test
    void sendFollowRequest_nullFollower() {
        User following = new UserTestBuilder().withId(2L).build();

        assertThrows(
                InvalidFollowException.class,
                () -> followService.sendFollowRequest(null, following)
        );

        verify(followRepository, never()).existsByFollowerAndFollowing(any(), any());
        verify(followFactory, never()).create(any(), any(), any());
        verify(followRepository, never()).save(any(Follow.class));
    }

    @Test
    void sendFollowRequest_nullFollowing() {
        User follower = new UserTestBuilder().withId(1L).build();

        assertThrows(
                InvalidFollowException.class,
                () -> followService.sendFollowRequest(follower, null)
        );

        verify(followRepository, never()).existsByFollowerAndFollowing(any(), any());
        verify(followFactory, never()).create(any(), any(), any());
        verify(followRepository, never()).save(any(Follow.class));
    }

    @Test
    void sendFollowRequest_sameUser() {
        User user = new UserTestBuilder().withId(1L).build();

        assertThrows(
                InvalidFollowException.class,
                () -> followService.sendFollowRequest(user, user)
        );

        verify(followRepository, never()).existsByFollowerAndFollowing(any(), any());
        verify(followFactory, never()).create(any(), any(), any());
        verify(followRepository, never()).save(any(Follow.class));
    }

    @Test
    void sendFollowRequest_alreadyExists() {
        User follower = new UserTestBuilder().withId(1L).build();
        User following = new UserTestBuilder().withId(2L).build();

        when(followRepository.existsByFollowerAndFollowing(follower, following)).thenReturn(true);

        assertThrows(
                FollowAlreadyExistsException.class,
                () -> followService.sendFollowRequest(follower, following)
        );

        verify(followRepository).existsByFollowerAndFollowing(follower, following);
        verify(followFactory, never()).create(any(), any(), any());
        verify(followRepository, never()).save(any(Follow.class));
    }

    @Test
    void getFollowById_ok() {
        Follow follow = new FollowTestBuilder().withId(1L).build();

        when(followRepository.findById(1L)).thenReturn(Optional.of(follow));

        Follow result = followService.getFollowById(1L);

        assertEquals(1L, result.getId());
        verify(followRepository).findById(1L);
    }

    @Test
    void getFollowById_notFound() {
        when(followRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(FollowNotFoundException.class, () -> followService.getFollowById(1L));

        verify(followRepository).findById(1L);
    }

    @Test
    void getPendingRequests_ok() {
        User user = new UserTestBuilder().withId(1L).build();

        Follow follow1 = new FollowTestBuilder()
                .withId(1L)
                .withFollowing(user)
                .withStatus(FollowStatus.PENDING)
                .build();

        Follow follow2 = new FollowTestBuilder()
                .withId(2L)
                .withFollowing(user)
                .withStatus(FollowStatus.PENDING)
                .build();

        when(followRepository.findByFollowingAndStatus(user, FollowStatus.PENDING))
                .thenReturn(List.of(follow1, follow2));

        List<Follow> result = followService.getPendingRequests(user);

        assertEquals(2, result.size());
        assertEquals(FollowStatus.PENDING, result.get(0).getStatus());

        verify(followRepository).findByFollowingAndStatus(user, FollowStatus.PENDING);
    }

    @Test
    void getFollowers_ok() {
        User user = new UserTestBuilder().withId(1L).build();

        Follow follow1 = new FollowTestBuilder()
                .withId(1L)
                .withFollowing(user)
                .withStatus(FollowStatus.ACCEPTED)
                .build();

        Follow follow2 = new FollowTestBuilder()
                .withId(2L)
                .withFollowing(user)
                .withStatus(FollowStatus.ACCEPTED)
                .build();

        when(followRepository.findByFollowingAndStatus(user, FollowStatus.ACCEPTED))
                .thenReturn(List.of(follow1, follow2));

        List<Follow> result = followService.getFollowers(user);

        assertEquals(2, result.size());
        assertEquals(FollowStatus.ACCEPTED, result.get(0).getStatus());

        verify(followRepository).findByFollowingAndStatus(user, FollowStatus.ACCEPTED);
    }

    @Test
    void getFollowing_ok() {
        User user = new UserTestBuilder().withId(1L).build();

        Follow follow1 = new FollowTestBuilder()
                .withId(1L)
                .withFollower(user)
                .withStatus(FollowStatus.ACCEPTED)
                .build();

        Follow follow2 = new FollowTestBuilder()
                .withId(2L)
                .withFollower(user)
                .withStatus(FollowStatus.ACCEPTED)
                .build();

        when(followRepository.findByFollowerAndStatus(user, FollowStatus.ACCEPTED))
                .thenReturn(List.of(follow1, follow2));

        List<Follow> result = followService.getFollowing(user);

        assertEquals(2, result.size());
        assertEquals(FollowStatus.ACCEPTED, result.get(0).getStatus());

        verify(followRepository).findByFollowerAndStatus(user, FollowStatus.ACCEPTED);
    }

    @Test
    void countFollowers_ok() {
        User user = new UserTestBuilder().withId(1L).build();

        when(followRepository.countByFollowingAndStatus(user, FollowStatus.ACCEPTED)).thenReturn(5L);

        long result = followService.countFollowers(user);

        assertEquals(5L, result);
        verify(followRepository).countByFollowingAndStatus(user, FollowStatus.ACCEPTED);
    }

    @Test
    void countFollowing_ok() {
        User user = new UserTestBuilder().withId(1L).build();

        when(followRepository.countByFollowerAndStatus(user, FollowStatus.ACCEPTED)).thenReturn(3L);

        long result = followService.countFollowing(user);

        assertEquals(3L, result);
        verify(followRepository).countByFollowerAndStatus(user, FollowStatus.ACCEPTED);
    }

    @Test
    void isFollowing_true() {
        User follower = new UserTestBuilder().withId(1L).build();
        User following = new UserTestBuilder().withId(2L).build();

        when(followRepository.existsByFollowerAndFollowingAndStatus(
                follower, following, FollowStatus.ACCEPTED
        )).thenReturn(true);

        boolean result = followService.isFollowing(follower, following);

        assertTrue(result);
        verify(followRepository).existsByFollowerAndFollowingAndStatus(
                follower, following, FollowStatus.ACCEPTED
        );
    }

    @Test
    void isFollowing_false() {
        User follower = new UserTestBuilder().withId(1L).build();
        User following = new UserTestBuilder().withId(2L).build();

        when(followRepository.existsByFollowerAndFollowingAndStatus(
                follower, following, FollowStatus.ACCEPTED
        )).thenReturn(false);

        boolean result = followService.isFollowing(follower, following);

        assertFalse(result);
        verify(followRepository).existsByFollowerAndFollowingAndStatus(
                follower, following, FollowStatus.ACCEPTED
        );
    }

    @Test
    void acceptFollowRequest_ok_createReverseFollow() {
        User follower = new UserTestBuilder().withId(1L).build();
        User following = new UserTestBuilder().withId(2L).build();

        Follow request = new FollowTestBuilder()
                .withId(10L)
                .withFollower(follower)
                .withFollowing(following)
                .withStatus(FollowStatus.PENDING)
                .build();

        Follow reverseFollow = new FollowTestBuilder()
                .withFollower(following)
                .withFollowing(follower)
                .withStatus(FollowStatus.ACCEPTED)
                .build();

        when(followRepository.findById(10L)).thenReturn(Optional.of(request));
        when(followRepository.existsByFollowerAndFollowing(following, follower)).thenReturn(false);
        when(followFactory.create(following, follower, FollowStatus.ACCEPTED)).thenReturn(reverseFollow);

        followService.acceptFollowRequest(10L);

        assertEquals(FollowStatus.ACCEPTED, request.getStatus());

        verify(followRepository).findById(10L);
        verify(followRepository).save(request);
        verify(followRepository).existsByFollowerAndFollowing(following, follower);
        verify(followFactory).create(following, follower, FollowStatus.ACCEPTED);
        verify(followRepository).save(reverseFollow);
    }

    @Test
    void acceptFollowRequest_ok_updateReverseFollow() {
        User follower = new UserTestBuilder().withId(1L).build();
        User following = new UserTestBuilder().withId(2L).build();

        Follow request = new FollowTestBuilder()
                .withId(10L)
                .withFollower(follower)
                .withFollowing(following)
                .withStatus(FollowStatus.PENDING)
                .build();

        Follow reverseFollow = new FollowTestBuilder()
                .withId(20L)
                .withFollower(following)
                .withFollowing(follower)
                .withStatus(FollowStatus.PENDING)
                .build();

        when(followRepository.findById(10L)).thenReturn(Optional.of(request));
        when(followRepository.existsByFollowerAndFollowing(following, follower)).thenReturn(true);
        when(followRepository.findByFollowerAndFollowing(following, follower))
                .thenReturn(Optional.of(reverseFollow));

        followService.acceptFollowRequest(10L);

        assertEquals(FollowStatus.ACCEPTED, request.getStatus());
        assertEquals(FollowStatus.ACCEPTED, reverseFollow.getStatus());

        verify(followRepository).findById(10L);
        verify(followRepository).save(request);
        verify(followRepository).existsByFollowerAndFollowing(following, follower);
        verify(followRepository).findByFollowerAndFollowing(following, follower);
        verify(followRepository).save(reverseFollow);
        verify(followFactory, never()).create(any(), any(), any());
    }

    @Test
    void acceptFollowRequest_notPending() {
        Follow request = new FollowTestBuilder()
                .withId(10L)
                .withStatus(FollowStatus.ACCEPTED)
                .build();

        when(followRepository.findById(10L)).thenReturn(Optional.of(request));

        assertThrows(
                InvalidFollowException.class,
                () -> followService.acceptFollowRequest(10L)
        );

        verify(followRepository).findById(10L);
        verify(followRepository, never()).save(any(Follow.class));
        verify(followRepository, never()).existsByFollowerAndFollowing(any(), any());
        verify(followFactory, never()).create(any(), any(), any());
    }

    @Test
    void rejectFollowRequest_ok_withoutReverse() {
        User follower = new UserTestBuilder().withId(1L).build();
        User following = new UserTestBuilder().withId(2L).build();

        Follow request = new FollowTestBuilder()
                .withId(10L)
                .withFollower(follower)
                .withFollowing(following)
                .withStatus(FollowStatus.PENDING)
                .build();

        when(followRepository.findById(10L)).thenReturn(Optional.of(request));
        when(followRepository.findByFollowerAndFollowing(following, follower)).thenReturn(Optional.empty());

        followService.rejectFollowRequest(10L);

        verify(followRepository).findById(10L);
        verify(followRepository).delete(request);
        verify(followRepository).findByFollowerAndFollowing(following, follower);
    }

    @Test
    void rejectFollowRequest_ok_withReverse() {
        User follower = new UserTestBuilder().withId(1L).build();
        User following = new UserTestBuilder().withId(2L).build();

        Follow request = new FollowTestBuilder()
                .withId(10L)
                .withFollower(follower)
                .withFollowing(following)
                .withStatus(FollowStatus.PENDING)
                .build();

        Follow reverse = new FollowTestBuilder()
                .withId(20L)
                .withFollower(following)
                .withFollowing(follower)
                .build();

        when(followRepository.findById(10L)).thenReturn(Optional.of(request));
        when(followRepository.findByFollowerAndFollowing(following, follower)).thenReturn(Optional.of(reverse));

        followService.rejectFollowRequest(10L);

        verify(followRepository).findById(10L);
        verify(followRepository).delete(request);
        verify(followRepository).findByFollowerAndFollowing(following, follower);
        verify(followRepository).delete(reverse);
    }

    @Test
    void rejectFollowRequest_notPending() {
        Follow request = new FollowTestBuilder()
                .withId(10L)
                .withStatus(FollowStatus.ACCEPTED)
                .build();

        when(followRepository.findById(10L)).thenReturn(Optional.of(request));

        assertThrows(
                InvalidFollowException.class,
                () -> followService.rejectFollowRequest(10L)
        );

        verify(followRepository).findById(10L);
        verify(followRepository, never()).delete(any(Follow.class));
        verify(followRepository, never()).findByFollowerAndFollowing(any(), any());
    }

    @Test
    void removeConnection_ok_bothSidesExist() {
        User firstUser = new UserTestBuilder().withId(1L).build();
        User secondUser = new UserTestBuilder().withId(2L).build();

        Follow first = new FollowTestBuilder()
                .withFollower(firstUser)
                .withFollowing(secondUser)
                .build();

        Follow second = new FollowTestBuilder()
                .withFollower(secondUser)
                .withFollowing(firstUser)
                .build();

        when(followRepository.findByFollowerAndFollowing(firstUser, secondUser))
                .thenReturn(Optional.of(first));
        when(followRepository.findByFollowerAndFollowing(secondUser, firstUser))
                .thenReturn(Optional.of(second));

        followService.removeConnection(firstUser, secondUser);

        verify(followRepository).findByFollowerAndFollowing(firstUser, secondUser);
        verify(followRepository).delete(first);
        verify(followRepository).findByFollowerAndFollowing(secondUser, firstUser);
        verify(followRepository).delete(second);
    }

    @Test
    void removeConnection_ok_oneSideMissing() {
        User firstUser = new UserTestBuilder().withId(1L).build();
        User secondUser = new UserTestBuilder().withId(2L).build();

        Follow first = new FollowTestBuilder()
                .withFollower(firstUser)
                .withFollowing(secondUser)
                .build();

        when(followRepository.findByFollowerAndFollowing(firstUser, secondUser))
                .thenReturn(Optional.of(first));
        when(followRepository.findByFollowerAndFollowing(secondUser, firstUser))
                .thenReturn(Optional.empty());

        followService.removeConnection(firstUser, secondUser);

        verify(followRepository).findByFollowerAndFollowing(firstUser, secondUser);
        verify(followRepository).delete(first);
        verify(followRepository).findByFollowerAndFollowing(secondUser, firstUser);
    }

    @Test
    void removeConnection_ok_bothSidesMissing() {
        User firstUser = new UserTestBuilder().withId(1L).build();
        User secondUser = new UserTestBuilder().withId(2L).build();

        when(followRepository.findByFollowerAndFollowing(firstUser, secondUser))
                .thenReturn(Optional.empty());
        when(followRepository.findByFollowerAndFollowing(secondUser, firstUser))
                .thenReturn(Optional.empty());

        followService.removeConnection(firstUser, secondUser);

        verify(followRepository).findByFollowerAndFollowing(firstUser, secondUser);
        verify(followRepository).findByFollowerAndFollowing(secondUser, firstUser);
        verify(followRepository, never()).delete(any(Follow.class));
    }
}