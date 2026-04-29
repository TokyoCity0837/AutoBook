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
import static org.mockito.ArgumentMatchers.any;

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

    private FollowResponse stubResponse(Long id, FollowStatus status) {
        return new FollowResponse(id, null, null, status, LocalDateTime.now());
    }

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
                new UserCardResponse(1L, "anton", "anton", null, null, false),
                new UserCardResponse(2L, "anna", "anna", null, null, false),
                FollowStatus.PENDING,
                LocalDateTime.now());

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
                new UserCardResponse(1L, "anton", "anton", null, null, false),
                new UserCardResponse(2L, "anna", "anna", null, null, false),
                FollowStatus.PENDING,
                LocalDateTime.now());

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
                LocalDateTime.now());

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
                LocalDateTime.now());

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

    // directFollow

    @Test
    void directFollow_ok() {
        User follower = new UserTestBuilder().withId(1L).build();
        User following = new UserTestBuilder().withId(2L).build();
        Follow follow = new Follow();
        follow.setId(10L);
        follow.setFollower(follower);
        follow.setFollowing(following);
        follow.setStatus(FollowStatus.ACCEPTED);

        when(followRepository.existsByFollowerAndFollowing(follower, following)).thenReturn(false);
        when(followFactory.create(follower, following, FollowStatus.ACCEPTED)).thenReturn(follow);
        when(followRepository.save(follow)).thenReturn(follow);
        when(followMapper.toResponse(follow)).thenReturn(stubResponse(10L, FollowStatus.ACCEPTED));

        FollowResponse res = followService.directFollow(follower, following);

        assertEquals(FollowStatus.ACCEPTED, res.status());
        verify(followRepository).save(follow);
    }

    @Test
    void directFollow_throwWhenAlreadyExists() {
        User follower = new UserTestBuilder().withId(1L).build();
        User following = new UserTestBuilder().withId(2L).build();
        when(followRepository.existsByFollowerAndFollowing(follower, following)).thenReturn(true);
        assertThrows(FollowAlreadyExistsException.class, () -> followService.directFollow(follower, following));
    }

    // validateFollowRequest errors

    @Test
    void sendFollowRequest_throwWhenSameUser() {
        User user = new UserTestBuilder().withId(1L).build();
        assertThrows(InvalidFollowException.class, () -> followService.sendFollowRequest(user, user));
    }

    @Test
    void sendFollowRequest_throwWhenFollowerNull() {
        User following = new UserTestBuilder().withId(2L).build();
        assertThrows(InvalidFollowException.class, () -> followService.sendFollowRequest(null, following));
    }

    // getFollowers / getFollowing

    @Test
    void getFollowers_ok() {
        User user = new UserTestBuilder().withId(1L).build();
        Follow f = new Follow();
        f.setId(5L);
        when(followRepository.findByFollowingAndStatus(user, FollowStatus.ACCEPTED)).thenReturn(List.of(f));
        when(followMapper.toResponse(f)).thenReturn(stubResponse(5L, FollowStatus.ACCEPTED));

        List<FollowResponse> res = followService.getFollowers(user);
        assertEquals(1, res.size());
    }

    @Test
    void getFollowing_ok() {
        User user = new UserTestBuilder().withId(1L).build();
        Follow f = new Follow();
        f.setId(6L);
        when(followRepository.findByFollowerAndStatus(user, FollowStatus.ACCEPTED)).thenReturn(List.of(f));
        when(followMapper.toResponse(f)).thenReturn(stubResponse(6L, FollowStatus.ACCEPTED));

        List<FollowResponse> res = followService.getFollowing(user);
        assertEquals(1, res.size());
    }

    // getFriends

    @Test
    void getFriends_returnsMutualFollows() {
        User user = new UserTestBuilder().withId(1L).build();
        User friend = new UserTestBuilder().withId(2L).build();

        Follow followerRecord = new Follow();
        followerRecord.setFollower(friend);
        followerRecord.setFollowing(user);

        Follow followingRecord = new Follow();
        followingRecord.setFollower(user);
        followingRecord.setFollowing(friend);

        when(followRepository.findByFollowingAndStatus(user, FollowStatus.ACCEPTED))
                .thenReturn(List.of(followerRecord));
        when(followRepository.findByFollowerAndStatus(user, FollowStatus.ACCEPTED))
                .thenReturn(List.of(followingRecord));
        when(followMapper.toResponse(followerRecord)).thenReturn(stubResponse(99L, FollowStatus.ACCEPTED));

        List<FollowResponse> friends = followService.getFriends(user);
        assertEquals(1, friends.size());
    }

    @Test
    void getFriends_returnsEmpty_whenNoMutual() {
        User user = new UserTestBuilder().withId(1L).build();
        User stranger = new UserTestBuilder().withId(99L).build();

        Follow followerRecord = new Follow();
        followerRecord.setFollower(stranger);
        followerRecord.setFollowing(user);
        // user does not follow stranger back

        when(followRepository.findByFollowingAndStatus(user, FollowStatus.ACCEPTED))
                .thenReturn(List.of(followerRecord));
        when(followRepository.findByFollowerAndStatus(user, FollowStatus.ACCEPTED)).thenReturn(List.of());

        List<FollowResponse> friends = followService.getFriends(user);
        assertTrue(friends.isEmpty());
    }

    // countFollowers / countFollowing
    @Test
    void countFollowers_ok() {
        User user = new UserTestBuilder().withId(1L).build();
        when(followRepository.countByFollowingAndStatus(user, FollowStatus.ACCEPTED)).thenReturn(5L);
        assertEquals(5L, followService.countFollowers(user));
    }

    @Test
    void countFollowing_ok() {
        User user = new UserTestBuilder().withId(1L).build();
        when(followRepository.countByFollowerAndStatus(user, FollowStatus.ACCEPTED)).thenReturn(3L);
        assertEquals(3L, followService.countFollowing(user));
    }

    // isFollowing / areFriends

    @Test
    void isFollowing_true() {
        User f1 = new UserTestBuilder().withId(1L).build();
        User f2 = new UserTestBuilder().withId(2L).build();
        when(followRepository.existsByFollowerAndFollowingAndStatus(f1, f2, FollowStatus.ACCEPTED)).thenReturn(true);
        assertTrue(followService.isFollowing(f1, f2));
    }

    @Test
    void areFriends_true() {
        User a = new UserTestBuilder().withId(1L).build();
        User b = new UserTestBuilder().withId(2L).build();
        when(followRepository.existsByFollowerAndFollowingAndStatus(a, b, FollowStatus.ACCEPTED)).thenReturn(true);
        when(followRepository.existsByFollowerAndFollowingAndStatus(b, a, FollowStatus.ACCEPTED)).thenReturn(true);
        assertTrue(followService.areFriends(a, b));
    }

    @Test
    void areFriends_false_whenOnlyOneWay() {
        User a = new UserTestBuilder().withId(1L).build();
        User b = new UserTestBuilder().withId(2L).build();
        when(followRepository.existsByFollowerAndFollowingAndStatus(a, b, FollowStatus.ACCEPTED)).thenReturn(true);
        when(followRepository.existsByFollowerAndFollowingAndStatus(b, a, FollowStatus.ACCEPTED)).thenReturn(false);
        assertFalse(followService.areFriends(a, b));
    }

    // unfollow

    @Test
    void unfollow_ok() {
        User follower = new UserTestBuilder().withId(1L).build();
        User following = new UserTestBuilder().withId(2L).build();
        Follow f = new Follow();
        when(followRepository.findByFollowerAndFollowing(follower, following)).thenReturn(Optional.of(f));

        followService.unfollow(follower, following);
        verify(followRepository).delete(f);
    }

    @Test
    void unfollow_noOp_whenNotFollowing() {
        User follower = new UserTestBuilder().withId(1L).build();
        User following = new UserTestBuilder().withId(2L).build();
        when(followRepository.findByFollowerAndFollowing(follower, following)).thenReturn(Optional.empty());

        followService.unfollow(follower, following);
        verify(followRepository, never()).delete(any());
    }

    // rejectFollowRequest

    @Test
    void rejectFollowRequest_ok() {
        Follow request = new Follow();
        request.setId(1L);
        request.setStatus(FollowStatus.PENDING);
        User follower = new UserTestBuilder().withId(1L).build();
        User following = new UserTestBuilder().withId(2L).build();
        request.setFollower(follower);
        request.setFollowing(following);

        when(followRepository.findById(1L)).thenReturn(Optional.of(request));
        when(followRepository.findByFollowerAndFollowing(following, follower)).thenReturn(Optional.empty());

        followService.rejectFollowRequest(1L);

        verify(followRepository).delete(request);
    }

    @Test
    void rejectFollowRequest_throwsIfNotPending() {
        Follow request = new Follow();
        request.setId(1L);
        request.setStatus(FollowStatus.ACCEPTED);
        when(followRepository.findById(1L)).thenReturn(Optional.of(request));

        assertThrows(InvalidFollowException.class, () -> followService.rejectFollowRequest(1L));
        verify(followRepository, never()).delete(any());
    }

    // acceptFollowRequest (reverseExists branch)

    @Test
    void acceptFollowRequest_whenReverseAlreadyExists() {
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
        reverseFollow.setStatus(FollowStatus.PENDING);

        when(followRepository.findById(100L)).thenReturn(Optional.of(request));
        when(followRepository.save(request)).thenReturn(request);
        when(followRepository.existsByFollowerAndFollowing(following, follower)).thenReturn(true);
        when(followRepository.findByFollowerAndFollowing(following, follower)).thenReturn(Optional.of(reverseFollow));
        when(followRepository.save(reverseFollow)).thenReturn(reverseFollow);
        when(followMapper.toResponse(request)).thenReturn(stubResponse(100L, FollowStatus.ACCEPTED));

        FollowResponse res = followService.acceptFollowRequest(100L);

        assertEquals(FollowStatus.ACCEPTED, res.status());
        // reverseFollow should be updated and saved
        assertEquals(FollowStatus.ACCEPTED, reverseFollow.getStatus());
        verify(followRepository).save(reverseFollow);
    }

    @Test
    void acceptFollowRequest_throwsIfNotPending() {
        Follow request = new Follow();
        request.setId(1L);
        request.setStatus(FollowStatus.ACCEPTED);
        when(followRepository.findById(1L)).thenReturn(Optional.of(request));

        assertThrows(InvalidFollowException.class, () -> followService.acceptFollowRequest(1L));
    }

    @Test
    void getFollowById_notFound() {
        when(followRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(FollowNotFoundException.class, () -> followService.getFollowById(999L));
    }
}