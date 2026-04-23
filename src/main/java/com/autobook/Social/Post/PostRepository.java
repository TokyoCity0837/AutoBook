package com.autobook.Social.Post;

import com.autobook.Enum.PostType;
import com.autobook.Social.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findAllByOrderByCreatedAtDesc();

    List<Post> findByPostTypeOrderByCreatedAtDesc(PostType postType);

    List<Post> findByAuthorOrderByCreatedAtDesc(User author);

    List<Post> findByAuthorAndPostTypeOrderByCreatedAtDesc(User author, PostType postType);

    List<Post> findByAuthorInAndPostTypeOrderByCreatedAtDesc(List<User> authors, PostType postType);

    Long countByAuthorAndPostType(User author, PostType postType);

    @Modifying
    @Query("UPDATE Post p SET p.likeCount = p.likeCount + 1 WHERE p.id = :postId")
    void incrementLikeCount(@Param("postId") Long postId);

    @Modifying
    @Query("UPDATE Post p SET p.likeCount = p.likeCount - 1 WHERE p.id = :postId AND p.likeCount > 0")
    void decrementLikeCount(@Param("postId") Long postId);

    @Modifying
    @Query("UPDATE Post p SET p.commentCount = p.commentCount + 1 WHERE p.id = :postId")
    void incrementCommentCount(@Param("postId") Long postId);

    @Modifying
    @Query("UPDATE Post p SET p.commentCount = p.commentCount - 1 WHERE p.id = :postId AND p.commentCount > 0")
    void decrementCommentCount(@Param("postId") Long postId);

    @Modifying
    @Query("UPDATE Post p SET p.repostCount = p.repostCount + 1 WHERE p.id = :postId")
    void incrementRepostCount(@Param("postId") Long postId);

    @Modifying
    @Query("UPDATE Post p SET p.repostCount = p.repostCount - 1 WHERE p.id = :postId AND p.repostCount > 0")
    void decrementRepostCount(@Param("postId") Long postId);
}