package com.autobook.Social.Follow;

import com.autobook.Enum.FollowStatus;
import com.autobook.Exception.FollowAlreadyExistsException;
import com.autobook.Exception.FollowNotFoundException;
import com.autobook.Exception.InvalidFollowException;
import com.autobook.Factory.FollowFactory;
import com.autobook.Social.Follow.DTO.Response.FollowResponse;
import com.autobook.Social.User.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FollowService {

    private final FollowRepository followRepository;
    private final FollowFactory followFactory;
    private final FollowMapper followMapper;

    @Transactional
    public FollowResponse sendFollowRequest(User follower, User following) {
        validateFollowRequest(follower, following);

        if (followRepository.existsByFollowerAndFollowing(follower, following)) {
            throw new FollowAlreadyExistsException();
        }

        Follow followRequest = followFactory.create(follower, following, FollowStatus.PENDING);
        Follow savedFollow = followRepository.save(followRequest);

        return followMapper.toResponse(savedFollow);
    }

    @Transactional
    public FollowResponse directFollow(User follower, User following) {
        validateFollowRequest(follower, following);

        if (followRepository.existsByFollowerAndFollowing(follower, following)) {
            throw new FollowAlreadyExistsException();
        }

        Follow followRequest = followFactory.create(follower, following, FollowStatus.ACCEPTED);
        Follow savedFollow = followRepository.save(followRequest);

        return followMapper.toResponse(savedFollow);
    }

    public FollowResponse getFollowById(Long followId) {
        return followMapper.toResponse(getFollowEntityById(followId));
    }

    public List<FollowResponse> getPendingRequests(User user) {
        return followRepository.findByFollowingAndStatus(user, FollowStatus.PENDING)
                .stream()
                .map(followMapper::toResponse)
                .toList();
    }

    public List<FollowResponse> getFollowers(User user) {
        return followRepository.findByFollowingAndStatus(user, FollowStatus.ACCEPTED)
                .stream()
                .map(followMapper::toResponse)
                .toList();
    }

    public List<FollowResponse> getFollowing(User user) {
        return followRepository.findByFollowerAndStatus(user, FollowStatus.ACCEPTED)
                .stream()
                .map(followMapper::toResponse)
                .toList();
    }

    public List<FollowResponse> getFriends(User user) {
        List<Follow> followers = followRepository.findByFollowingAndStatus(user, FollowStatus.ACCEPTED);
        List<Follow> following = followRepository.findByFollowerAndStatus(user, FollowStatus.ACCEPTED);
        List<Long> followingIds = following.stream().map(f -> f.getFollowing().getId()).toList();

        return followers.stream()
                .filter(f -> followingIds.contains(f.getFollower().getId()))
                .map(followMapper::toResponse)
                .toList();
    }

    public long countFollowers(User user) {
        return followRepository.countByFollowingAndStatus(user, FollowStatus.ACCEPTED);
    }

    public long countFollowing(User user) {
        return followRepository.countByFollowerAndStatus(user, FollowStatus.ACCEPTED);
    }

    public boolean isFollowing(User follower, User following) {
        return followRepository.existsByFollowerAndFollowingAndStatus(
                follower,
                following,
                FollowStatus.ACCEPTED
        );
    }

    @Transactional
    public FollowResponse acceptFollowRequest(Long followId) {
        Follow request = getFollowEntityById(followId);

        if (request.getStatus() != FollowStatus.PENDING) {
            throw new InvalidFollowException();
        }

        request.setStatus(FollowStatus.ACCEPTED);
        Follow savedRequest = followRepository.save(request);

        boolean reverseExists = followRepository.existsByFollowerAndFollowing(
                request.getFollowing(),
                request.getFollower()
        );

        if (!reverseExists) {
            Follow reverseFollow = followFactory.create(
                    request.getFollowing(),
                    request.getFollower(),
                    FollowStatus.ACCEPTED
            );
            followRepository.save(reverseFollow);
        } else {
            Follow reverseFollow = followRepository.findByFollowerAndFollowing(
                    request.getFollowing(),
                    request.getFollower()
            ).orElseThrow(FollowNotFoundException::new);

            reverseFollow.setStatus(FollowStatus.ACCEPTED);
            followRepository.save(reverseFollow);
        }

        return followMapper.toResponse(savedRequest);
    }

    @Transactional
    public void rejectFollowRequest(Long followId) {
        Follow request = getFollowEntityById(followId);

        if (request.getStatus() != FollowStatus.PENDING) {
            throw new InvalidFollowException();
        }

        followRepository.delete(request);

        followRepository.findByFollowerAndFollowing(request.getFollowing(), request.getFollower())
                .ifPresent(followRepository::delete);
    }

    @Transactional
    public void removeConnection(User firstUser, User secondUser) {
        followRepository.findByFollowerAndFollowing(firstUser, secondUser)
                .ifPresent(followRepository::delete);

        followRepository.findByFollowerAndFollowing(secondUser, firstUser)
                .ifPresent(followRepository::delete);
    }

    private Follow getFollowEntityById(Long followId) {
        return followRepository.findById(followId)
                .orElseThrow(() -> new FollowNotFoundException(followId));
    }

    private void validateFollowRequest(User follower, User following) {
        if (follower == null || following == null) {
            throw new InvalidFollowException();
        }

        if (follower.getId().equals(following.getId())) {
            throw new InvalidFollowException();
        }
    }
}