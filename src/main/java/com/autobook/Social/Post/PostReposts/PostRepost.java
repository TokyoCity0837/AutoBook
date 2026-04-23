package com.autobook.Social.Post.PostReposts;

import com.autobook.Social.Post.Post;
import com.autobook.Social.User.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "post_reposts")
@NoArgsConstructor
@Getter
public class PostRepost {

    @EmbeddedId
    private PostRepostId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("postId")
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public PostRepost(User user, Post post) {
        this.id = new PostRepostId(user.getId(), post.getId());
        this.user = user;
        this.post = post;
    }

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}