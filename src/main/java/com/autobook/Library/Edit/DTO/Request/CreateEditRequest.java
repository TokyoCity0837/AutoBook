package com.autobook.Library.Edit.DTO.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateEditRequest(
        @NotBlank(message = "Message cannot be blank")
        @Size(max = 1000, message = "Message cannot be longer than 1000 characters")
        String message
) {}