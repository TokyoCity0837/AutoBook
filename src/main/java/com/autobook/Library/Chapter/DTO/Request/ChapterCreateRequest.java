package com.autobook.Library.Chapter.DTO.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChapterCreateRequest(
        @NotBlank(message = "Chapter title cannot be empty")
        @Size(max = 255, message = "Chapter title cannot be longer than 255 characters")
        String title,

        String content
) {}