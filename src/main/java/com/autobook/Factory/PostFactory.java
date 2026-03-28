package com.autobook.Factory;

import com.autobook.Enum.PostType;
import com.autobook.Social.Post.Post;
import com.autobook.Social.User.User;
import org.springframework.stereotype.Component;

@Component
public class PostFactory {

    public Post create(String content, User author, PostType postType) {
        Post post = new Post();
        post.setContent(content);
        post.setAuthor(author);
        post.setPostType(postType);
        return post;
    }
}