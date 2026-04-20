package com.autobook.Social.Post.DTO.Response;

import com.autobook.Enum.PostType;
import com.autobook.Social.Comment.DTO.Response.CommentResponse;
import com.autobook.Social.User.DTO.Response.UserPostDetailsResponse;

import java.time.LocalDateTime;
import java.util.List;

public record PostDetailsResponse(
        Long id,
        String content,
        UserPostDetailsResponse author,
        PostType postType,
        String imageUrl,
        boolean hasImage,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        int likeCount,
        int commentCount,
        int repostCount,
        List<CommentResponse> comments
) {}