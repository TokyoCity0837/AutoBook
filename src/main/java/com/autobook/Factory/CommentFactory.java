package com.autobook.Factory;

import com.autobook.Social.Comment.Comment;
import com.autobook.Social.Post.Post;
import com.autobook.Social.User.User;
import org.springframework.stereotype.Component;

@Component
public class CommentFactory {

    public Comment create(String content, User author, Post post) {
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setAuthor(author);
        comment.setPost(post);
        return comment;
    }
}