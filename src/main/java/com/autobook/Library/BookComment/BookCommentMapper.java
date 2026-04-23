package com.autobook.Library.BookComment;

import com.autobook.Library.BookComment.DTO.Response.BookCommentResponse;
import com.autobook.Social.User.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BookCommentMapper {

    private final UserMapper userMapper;

    public BookCommentResponse toResponse(BookComment comment) {
        return toResponseLevel(comment, new ArrayList<>());
    }

    public BookCommentResponse toResponseLevel(BookComment comment, List<Long> seenIds) {
        if (seenIds.contains(comment.getId())) return null;
        seenIds.add(comment.getId());

        return new BookCommentResponse(
                comment.getId(),
                comment.getContent(),
                userMapper.toCardResponse(comment.getAuthor()),
                comment.getCreatedAt(),
                comment.getParentComment() != null ? comment.getParentComment().getId() : null,
                new ArrayList<>(),
                comment.getLikeCount()
        );
    }
}
