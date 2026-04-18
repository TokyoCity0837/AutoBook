package com.autobook.Library.Chapter.DTO.Response;

import java.time.LocalDateTime;

public record ChapterResponse(
        Long id,
        String title,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}