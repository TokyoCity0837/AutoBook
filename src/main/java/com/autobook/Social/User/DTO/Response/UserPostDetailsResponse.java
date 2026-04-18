package com.autobook.Social.User.DTO.Response;

import com.autobook.Enum.UserRole;

public record UserPostDetailsResponse(
        Long id,
        String visibleName,
        String profileImage,
        String bio,
        UserRole userRole
) {}