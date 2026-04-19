package com.autobook.Social.Post.DTO.Request;

import com.autobook.Enum.PostType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreatePostRequest(

        @NotBlank(message = "Post content cannot be blank")
        @Size(max = 5000, message = "Post content is too long")
        String content,

        @NotNull
        PostType postType,

        String imageUrl

) {}