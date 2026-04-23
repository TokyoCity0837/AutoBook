package com.autobook.Social.User.DTO.Response;

import com.autobook.Social.Post.DTO.Response.PostResponse;

import java.time.LocalDateTime;

public record ProfilePostItemResponse(
        String type,
        PostResponse post,
        UserCardResponse repostedBy,
        LocalDateTime repostedAt,
        LocalDateTime activityAt
) {}