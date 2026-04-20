package com.autobook.Library.BookComment;

import jakarta.validation.constraints.NotBlank;

public record CreateBookCommentRequest(
        @NotBlank(message = "Comment content cannot be empty")
        String content,
        Long parentId
) {}
