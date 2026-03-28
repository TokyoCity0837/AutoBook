package com.autobook.Social.Follow;

import com.autobook.Enum.FollowStatus;
import com.autobook.Exception.FollowAlreadyExistsException;
import com.autobook.Exception.FollowNotFoundException;
import com.autobook.Exception.InvalidFollowException;
import com.autobook.Factory.FollowFactory;
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

    @Transactional
    public Follow sendFollowRequest(User follower, User following) {
        validateFollowRequest(follower, following);

        if (followRepository.existsByFollowerAndFollowing(follower, following)) {
            throw new FollowAlreadyExistsException();
        }

        Follow followRequest = followFactory.create(follower, following, FollowStatus.PENDING);
        return followRepository.save(followRequest);
    }

    public Follow getFollowById(Long followId) {
        return followRepository.findById(followId)
                .orElseThrow(() -> new FollowNotFoundException(followId));
    }

    public List<Follow> getPendingRequests(User user) {
        return followRepository.findByFollowingAndStatus(user, FollowStatus.PENDING);
    }

    public List<Follow> getFollowers(User user) {
        return followRepository.findByFollowingAndStatus(user, FollowStatus.ACCEPTED);
    }

    public List<Follow> getFollowing(User user) {
        return followRepository.findByFollowerAndStatus(user, FollowStatus.ACCEPTED);
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
    public void acceptFollowRequest(Long followId) {
        Follow request = getFollowById(followId);

        if (request.getStatus() != FollowStatus.PENDING) {
            throw new InvalidFollowException();
        }

        request.setStatus(FollowStatus.ACCEPTED);
        followRepository.save(request);

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
    }

    @Transactional
    public void rejectFollowRequest(Long followId) {
        Follow request = getFollowById(followId);

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

    private void validateFollowRequest(User follower, User following) {
        if (follower == null || following == null) {
            throw new InvalidFollowException();
        }

        if (follower.getId().equals(following.getId())) {
            throw new InvalidFollowException();
        }
    }
}