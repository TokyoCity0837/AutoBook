package com.autobook.util;
import com.autobook.Social.User.User;
import com.autobook.Social.Post.Post;
import com.autobook.Social.Comment.Comment;

public class CommentTestBuilder {
    
    private long id = 1L;
    private String content = "default content";
    private User author = new UserTestBuilder().build();
    private Post post = new PostTestBuilder().build();

    public CommentTestBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public CommentTestBuilder withContent(String content) {
        this.content = content;
        return this;
    }

    public CommentTestBuilder withAuthor(User author) {
        this.author = author;
        return this;
    }

    public CommentTestBuilder withPostConnected(Post post) {
        this.post = post;
        return this;
    }

    public Comment build(){
        Comment comment = new Comment();
        comment.setId(id);
        comment.setAuthor(author);
        comment.setContent(content);
        comment.setPost(post);
        return comment;
    }

}
