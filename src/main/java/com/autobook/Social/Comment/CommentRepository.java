package com.autobook.Social.Comment;

import org.springframework.data.jpa.repository.JpaRepository;
import com.autobook.Social.Post.Post;
import com.autobook.Social.User.User;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByPostOrderByCreatedAtDesc(Post post);

    List<Comment> findByAuthorOrderByCreatedAtDesc(User author);

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("UPDATE Comment c SET c.likeCount = c.likeCount + 1 WHERE c.id = :id")
    void incrementLikeCount(@org.springframework.data.repository.query.Param("id") Long id);

    void deleteByIdAndAuthor(Long id, User author);
}