package com.autobook.Social.Follow.DTO.Request;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SendFollowRequestTest {
    @Test
    void testRecord() {
        SendFollowRequest r = new SendFollowRequest(1L, 2L);
        assertEquals(1L, r.followerId());
        assertEquals(2L, r.followingId());
    }
}
