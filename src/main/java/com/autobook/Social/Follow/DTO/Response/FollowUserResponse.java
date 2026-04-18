package com.autobook.Social.Follow.DTO.Response;

import com.autobook.Social.User.DTO.Response.UserCardResponse;

public record FollowUserResponse(
        Long followId,
        UserCardResponse user
) {}