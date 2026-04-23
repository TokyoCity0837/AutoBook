package com.autobook.Social.User.DTO.Response;

import com.autobook.Enum.UserRole;


public record UserCardResponse (
    Long id,
    String visibleName,
    String username,
    String profileImage,
    UserRole userRole,
    boolean isFriend

){}
