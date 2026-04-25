package com.autobook.MapperTest;

import com.autobook.Enum.FollowStatus;
import com.autobook.Social.Follow.Follow;
import com.autobook.Social.Follow.FollowMapper;
import com.autobook.Social.Follow.DTO.Response.FollowResponse;
import com.autobook.Social.User.User;
import com.autobook.util.UserTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FollowMapperTest {

    private FollowMapper followMapper;

    @BeforeEach
    void setUp() {
        followMapper = new FollowMapper();
    }

    @Test
    void toResponse() {
        User follower = new UserTestBuilder().withId(1L).withUsername("andrii").build();
        follower.setVisibleName("Andrii");
        User following = new UserTestBuilder().withId(2L).withUsername("anton").build();
        following.setVisibleName("Anton");

        Follow follow = new Follow();
        follow.setId(10L);
        follow.setFollower(follower);
        follow.setFollowing(following);
        follow.setStatus(FollowStatus.PENDING);

        FollowResponse response = followMapper.toResponse(follow);

        assertNotNull(response);
        assertEquals(10L, response.id());
        assertEquals(FollowStatus.PENDING, response.status());
        assertEquals("andrii", response.follower().username());
        assertEquals("anton", response.following().username());
    }
}
