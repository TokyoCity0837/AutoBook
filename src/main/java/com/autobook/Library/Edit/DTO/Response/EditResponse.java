package com.autobook.Library.Edit.DTO.Response;

import com.autobook.Enum.EditStatus;
import com.autobook.Social.User.DTO.Response.UserCardResponse;

import java.time.LocalDateTime;

public record EditResponse(
        Long id,
        UserCardResponse fromUser,
        String message,
        EditStatus status,
        LocalDateTime createdAt
) {}