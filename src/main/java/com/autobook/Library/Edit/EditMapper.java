package com.autobook.Library.Edit;

import com.autobook.Library.Edit.DTO.Response.EditResponse;
import com.autobook.Social.User.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EditMapper {

    private final UserMapper userMapper;

    public EditResponse toResponse(Edit edit) {
        return new EditResponse(
                edit.getId(),
                userMapper.toCardResponse(edit.getFromUser()),
                edit.getMessage(),
                edit.getStatus(),
                edit.getCreatedAt()
        );
    }
}