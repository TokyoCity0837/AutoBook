package com.autobook.Library.BookComment.DTO.Response;

import com.autobook.Social.User.DTO.Response.UserCardResponse;
import java.time.LocalDateTime;
import java.util.List;

public record BookCommentResponse(
        Long id,
        String content,
        UserCardResponse author,
        LocalDateTime createdAt,
        Long parentId,
        List<BookCommentResponse> replies,
        int likes
) {}
