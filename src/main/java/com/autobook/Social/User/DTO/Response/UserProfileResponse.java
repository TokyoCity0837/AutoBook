package com.autobook.Social.User.DTO.Response;

import com.autobook.Enum.PrivacyType;
import com.autobook.Enum.UserRole;
import com.autobook.Library.Book.DTO.Response.BookCardResponse;
import com.autobook.Social.Post.DTO.Response.PostResponse;

import java.time.LocalDateTime;
import java.util.List;

public record UserProfileResponse(
    Long id,
    String username,
    String visibleName,
    String bio,
    String profileImage,
    PrivacyType privacy,
    LocalDateTime createdAt,
    UserRole userRole,
    long followers,
    long friends,
    List<BookCardResponse> books,
    List<PostResponse> posts
) {}