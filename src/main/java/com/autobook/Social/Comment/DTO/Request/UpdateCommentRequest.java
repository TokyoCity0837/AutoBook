package com.autobook.Social.Comment.DTO.Request;

import jakarta.validation.constraints.NotBlank;

public record UpdateCommentRequest(
        @NotBlank(message = "Comment content cannot be empty")
        String content
) {}