package com.autobook.Factory;

import com.autobook.Enum.FollowStatus;
import com.autobook.Social.Follow.Follow;
import com.autobook.Social.User.User;
import org.springframework.stereotype.Component;

@Component
public class FollowFactory {

    public Follow create(User follower, User following, FollowStatus status) {
        Follow follow = new Follow();
        follow.setFollower(follower);
        follow.setFollowing(following);
        follow.setStatus(status);
        return follow;
    }
}