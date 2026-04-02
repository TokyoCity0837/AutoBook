package com.autobook.util;

import com.autobook.Enum.PostType;
import com.autobook.Social.Post.Post;
import com.autobook.Social.Comment.Comment;
import com.autobook.Social.User.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PostTestBuilder {

    private Long id = 1L;
    private String content = "Hello bro";
    private User author = new UserTestBuilder().build();
    private PostType postType = PostType.FEED;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int likeCount = 0;
    private int commentCount = 0;
    private int repostCount = 0;
    private List<Comment> comments = new ArrayList<>();

    public PostTestBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public PostTestBuilder withContent(String content) {
        this.content = content;
        return this;
    }

    public PostTestBuilder withAuthor(User author) {
        this.author = author;
        return this;
    }

    public PostTestBuilder withPostType(PostType postType) {
        this.postType = postType;
        return this;
    }

    public PostTestBuilder withCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public PostTestBuilder withUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public PostTestBuilder withLikeCount(int likeCount) {
        this.likeCount = likeCount;
        return this;
    }

    public PostTestBuilder withCommentCount(int commentCount) {
        this.commentCount = commentCount;
        return this;
    }

    public PostTestBuilder withRepostCount(int repostCount) {
        this.repostCount = repostCount;
        return this;
    }

    public PostTestBuilder withComments(List<Comment> comments) {
        this.comments = comments;
        return this;
    }

    public PostTestBuilder addComment(Comment comment) {
        this.comments.add(comment);
        return this;
    }

    public Post build() {
        Post post = new Post();
        post.setId(id);
        post.setContent(content);
        post.setAuthor(author);
        post.setPostType(postType);
        post.setCreatedAt(createdAt);
        post.setUpdatedAt(updatedAt);
        post.setLikeCount(likeCount);
        post.setCommentCount(commentCount);
        post.setRepostCount(repostCount);
        post.setComments(comments);
        return post;
    }
}