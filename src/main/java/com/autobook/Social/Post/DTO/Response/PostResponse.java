package com.autobook.Social.Post.DTO.Response;

import com.autobook.Enum.PostType;
import com.autobook.Social.User.DTO.Response.UserCardResponse;

import java.time.LocalDateTime;

public record PostResponse(
        
        Long id,
        String content,
        UserCardResponse author,
        PostType postType,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        int likeCount,
        int commentCount,
        int repostCount

) {}