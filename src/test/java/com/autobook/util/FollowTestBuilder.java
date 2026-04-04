package com.autobook.util;

import com.autobook.Enum.FollowStatus;
import com.autobook.Social.Follow.Follow;
import com.autobook.Social.User.User;

import java.time.LocalDateTime;

public class FollowTestBuilder {

    private Long id = 1L;
    private User follower = new UserTestBuilder()
            .withId(1L)
            .withUsername("follower")
            .build();
    private User following = new UserTestBuilder()
            .withId(2L)
            .withUsername("following")
            .build();
    private FollowStatus status = FollowStatus.PENDING;
    private LocalDateTime createdAt = LocalDateTime.now();

    public FollowTestBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public FollowTestBuilder withFollower(User follower) {
        this.follower = follower;
        return this;
    }

    public FollowTestBuilder withFollowing(User following) {
        this.following = following;
        return this;
    }

    public FollowTestBuilder withStatus(FollowStatus status) {
        this.status = status;
        return this;
    }

    public FollowTestBuilder withCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Follow build() {
        Follow follow = new Follow();
        follow.setId(id);
        follow.setFollower(follower);
        follow.setFollowing(following);
        follow.setStatus(status);
        follow.setCreatedAt(createdAt);
        return follow;
    }
}