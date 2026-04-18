package com.autobook.Library.Book.DTO.Response;

import com.autobook.Library.Edit.DTO.Response.EditResponse;
import com.autobook.Social.User.DTO.Response.UserCardResponse;

import java.util.List;

public record BookDetailsResponse(
        Long id,
        String title,
        String coverImage,
        UserCardResponse author,
        String description,
        List<EditResponse> editRequests
) {}