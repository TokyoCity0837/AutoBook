package com.autobook.Social.Comment;

import com.autobook.Social.Comment.DTO.Response.CommentResponse;
import com.autobook.Social.User.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class CommentMapper {

    private final UserMapper userMapper;

    public CommentResponse toResponse(Comment comment) {
        return toResponseLevel(comment, new ArrayList<>());
    }
    
    // Quick helper to avoid circular recursion if database gets corrupted loop, but realistically tree is small.
    // Fetch lazy loaded children if any
    public CommentResponse toResponseLevel(Comment comment, List<Long> seenIds) {
        if (seenIds.contains(comment.getId())) return null;
        seenIds.add(comment.getId());
        
        // Find mapped replies for the entity if we actually loaded them inside spring context
        // Spring data JPA doesn't map children upwards automatically unless we use @OneToMany mappedBy
        // However, if we just mapped them via repository fetching, we can assign an empty list 
        // to be hydrated by the Service. Let's just return empty list here and force Service to build tree!
        return new CommentResponse(
                comment.getId(),
                comment.getContent(),
                userMapper.toCardResponse(comment.getAuthor()),
                comment.getCreatedAt(),
                comment.getCreatedAt(), // updatedAt placeholder
                comment.getParentComment() != null ? comment.getParentComment().getId() : null,
                new ArrayList<>(), // Service will hydrate
                comment.getLikeCount()
        );
    }
}