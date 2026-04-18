package com.autobook.Social.Follow.DTO.Request;

public record SendFollowRequest(
        Long followerId,
        Long followingId
) {}