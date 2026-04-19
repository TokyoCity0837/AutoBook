package com.autobook.Library.Book.DTO.Response;

import com.autobook.Enum.PrivacyType;
import com.autobook.Library.Edit.DTO.Response.EditResponse;
import com.autobook.Social.User.DTO.Response.UserCardResponse;

import java.time.LocalDateTime;
import java.util.List;

public record BookDetailsResponse(
        Long id,
        String title,
        String coverImage,
        UserCardResponse author,
        String description,
        String genre,
        PrivacyType privacy,
        String font,
        Integer fontSize,
        String lineHeight,
        Integer paraStyle,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<EditResponse> editRequests
) {}