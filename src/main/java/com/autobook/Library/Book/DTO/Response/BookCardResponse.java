package com.autobook.Library.Book.DTO.Response;

import com.autobook.Social.User.DTO.Response.UserCardResponse;

public record BookCardResponse(
        Long id,
        String title,
        String coverImage,
        UserCardResponse author
) {}