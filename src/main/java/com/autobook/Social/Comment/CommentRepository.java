package com.autobook.Social.Comment;

import org.springframework.data.jpa.repository.JpaRepository;
import com.autobook.Social.Post.Post;
import com.autobook.Social.User.User;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByPostOrderByCreatedAtDesc(Post post);

    List<Comment> findByAuthorOrderByCreatedAtDesc(User author);

    void deleteByIdAndAuthor(Long id, User author);
}