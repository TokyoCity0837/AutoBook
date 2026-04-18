package com.autobook.Social.Follow.DTO.Response;

import com.autobook.Enum.FollowStatus;
import com.autobook.Social.User.DTO.Response.UserCardResponse;

import java.time.LocalDateTime;

public record FollowResponse(
        Long id,
        UserCardResponse follower,
        UserCardResponse following,
        FollowStatus status,
        LocalDateTime createdAt
) {}