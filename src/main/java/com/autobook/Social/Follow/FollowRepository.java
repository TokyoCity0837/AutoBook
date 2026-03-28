package com.autobook.Social.Follow;

import com.autobook.Enum.FollowStatus;
import com.autobook.Social.User.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    List<Follow> findByFollower(User follower);

    List<Follow> findByFollowing(User following);

    List<Follow> findByFollowingAndStatus(User following, FollowStatus status);

    List<Follow> findByFollowerAndStatus(User follower, FollowStatus status);

    Optional<Follow> findByFollowerAndFollowing(User follower, User following);

    boolean existsByFollowerAndFollowing(User follower, User following);

    boolean existsByFollowerAndFollowingAndStatus(User follower, User following, FollowStatus status);

    long countByFollowingAndStatus(User following, FollowStatus status);

    long countByFollowerAndStatus(User follower, FollowStatus status);

    void deleteByFollowerAndFollowing(User follower, User following);
}