package com.autobook.Library.Book.DTO.Request;

import com.autobook.Enum.PrivacyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateBookRequest(

        @NotBlank(message = "Book title cannot be empty")
        @Size(max = 255, message = "Book title cannot be longer than 255 characters")
        String title,

        String description,

        @Size(max = 50, message = "Genre cannot be longer than 50 characters")
        String genre,

        PrivacyType privacy,

        String coverImage
) {}