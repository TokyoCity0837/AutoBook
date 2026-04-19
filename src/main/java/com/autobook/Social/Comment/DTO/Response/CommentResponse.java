package com.autobook.Social.Comment.DTO.Response;

import com.autobook.Social.User.DTO.Response.UserCardResponse;

import java.time.LocalDateTime;
import java.util.List;

public record CommentResponse(
        Long id,
        String content,
        UserCardResponse author,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long parentId,
        List<CommentResponse> replies,
        int likes
) {}