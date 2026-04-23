package com.autobook.Social.User.DTO.Request;

import com.autobook.Enum.PrivacyType;

public record UserUpdateRequest (

    String visibleName,
    String bio,
    String profileImage,
    PrivacyType privacyType,
    String username
){}
