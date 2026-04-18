package com.autobook.Social.Comment;

import com.autobook.Social.Comment.DTO.Response.CommentResponse;
import com.autobook.Social.User.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentMapper {

    private final UserMapper userMapper;

    public CommentResponse toResponse(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getContent(),
                userMapper.toCardResponse(comment.getAuthor()),
                comment.getCreatedAt()
        );
    }
}