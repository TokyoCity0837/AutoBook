package com.autobook.Social.Post.DTO.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdatePostRequest(

        @NotBlank(message = "Post content cannot be blank")
        @Size(max = 5000, message = "Post content is too long")
        String content

) {}