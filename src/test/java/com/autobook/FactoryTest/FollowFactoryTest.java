package com.autobook.FactoryTest;

import com.autobook.Enum.FollowStatus;
import com.autobook.Factory.FollowFactory;
import com.autobook.Social.Follow.Follow;
import com.autobook.Social.User.User;
import com.autobook.util.UserTestBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FollowFactoryTest {

    private final FollowFactory followFactory = new FollowFactory();

    @Test
    void create_pending_ok() {
        User follower = new UserTestBuilder()
                .withId(1L)
                .withUsername("anton1")
                .build();

        User following = new UserTestBuilder()
                .withId(2L)
                .withUsername("anton2")
                .build();

        Follow result = followFactory.create(follower, following, FollowStatus.PENDING);

        assertNotNull(result);
        assertEquals(follower, result.getFollower());
        assertEquals(following, result.getFollowing());
        assertEquals(FollowStatus.PENDING, result.getStatus());
    }

    @Test
    void create_accepted_ok() {
        User follower = new UserTestBuilder()
                .withId(1L)
                .withUsername("anton1")
                .build();

        User following = new UserTestBuilder()
                .withId(2L)
                .withUsername("anton2")
                .build();

        Follow result = followFactory.create(follower, following, FollowStatus.ACCEPTED);

        assertNotNull(result);
        assertEquals(follower, result.getFollower());
        assertEquals(following, result.getFollowing());
        assertEquals(FollowStatus.ACCEPTED, result.getStatus());
    }
}