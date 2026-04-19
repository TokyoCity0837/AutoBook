package com.autobook.Library.Book.DTO.Request;

import com.autobook.Enum.PrivacyType;
import jakarta.validation.constraints.Size;

public record UpdateBookRequest(

        String title,

        String description,

        @Size(max = 50, message = "Genre cannot be longer than 50 characters")
        String genre,

        PrivacyType privacy,

        String coverImage,

        String font,

        Integer fontSize,

        String lineHeight,

        Integer paraStyle
) {}