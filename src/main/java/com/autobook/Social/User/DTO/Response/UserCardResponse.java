package com.autobook.Social.User.DTO.Response;

import com.autobook.Enum.UserRole;


public record UserCardResponse (
    Long id,
    String visibleName,
    String profileImage,
    UserRole userRole

){}
