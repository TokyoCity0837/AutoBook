package com.autobook.Factory;

import com.autobook.Enum.PostType;
import com.autobook.Social.Post.Post;
import com.autobook.Social.User.User;
import org.springframework.stereotype.Component;

/**
 * Factory component for creating {@link Post} instances.
 * <p>
 * Encapsulates post object construction so that service classes
 * remain free of entity-building logic. Implements the Factory pattern.
 */
@Component
public class PostFactory {

    public Post create(String content, User author, PostType postType, String imageUrl) {
        Post post = new Post();
        post.setContent(content);
        post.setAuthor(author);
        post.setPostType(postType);
        post.setImageUrl(imageUrl);
        return post;
    }
}