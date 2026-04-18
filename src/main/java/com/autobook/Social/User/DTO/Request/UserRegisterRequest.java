package com.autobook.Social.User.DTO.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRegisterRequest (

    @NotBlank
    @Size(min = 5, max = 50)
    String username,

    @NotBlank
    @Size(min = 5, max = 30)
    String visibleName,

    @NotBlank
    @Size(min = 5, max = 50)
    String email,

    @NotBlank
    @Size(min = 5, max = 75)
    String password

){}
