package com.autobook.Social.Post;

import com.autobook.Social.Comment.CommentMapper;
import com.autobook.Social.Post.DTO.Response.PostDetailsResponse;
import com.autobook.Social.Post.DTO.Response.PostResponse;
import com.autobook.Social.User.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostMapper {

    private final UserMapper userMapper;
    private final CommentMapper commentMapper;

    public PostResponse toResponse(Post post, boolean likedByMe, boolean repostedByMe) {
        return new PostResponse(
                post.getId(),
                post.getContent(),
                userMapper.toCardResponse(post.getAuthor()),
                post.getPostType(),
                post.getImageUrl(),
                post.getImageUrl() != null && !post.getImageUrl().isEmpty(),
                post.getCreatedAt(),
                post.getUpdatedAt(),
                post.getLikeCount(),
                post.getCommentCount(),
                post.getRepostCount(),
                likedByMe,
                repostedByMe
        );
    }



    public PostDetailsResponse toDetailsResponse(Post post, boolean likedByMe, boolean repostedByMe) {
        return new PostDetailsResponse(
                post.getId(),
                post.getContent(),
                userMapper.toPostDetailsResponse(post.getAuthor()),
                post.getPostType(),
                post.getImageUrl(),
                post.getImageUrl() != null && !post.getImageUrl().isEmpty(),
                post.getCreatedAt(),
                post.getUpdatedAt(),
                post.getLikeCount(),
                post.getCommentCount(),
                post.getRepostCount(),
                post.getComments().stream()
                        .map(commentMapper::toResponse)
                        .toList(),
                likedByMe,
                repostedByMe
        );
    }
}