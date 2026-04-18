package com.autobook.Social.Comment.DTO.Response;

import com.autobook.Social.User.DTO.Response.UserCardResponse;

import java.time.LocalDateTime;

public record CommentResponse(
        Long id,
        String content,
        UserCardResponse author,
        LocalDateTime createdAt
) {}