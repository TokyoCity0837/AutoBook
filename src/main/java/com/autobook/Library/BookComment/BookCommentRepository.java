package com.autobook.Library.BookComment;

import com.autobook.Library.Book.Book;
import com.autobook.Social.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookCommentRepository extends JpaRepository<BookComment, Long> {
    List<BookComment> findByBookOrderByCreatedAtDesc(Book book);
    List<BookComment> findByAuthorOrderByCreatedAtDesc(User author);

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("UPDATE BookComment c SET c.likeCount = c.likeCount + 1 WHERE c.id = :id")
    void incrementLikeCount(@org.springframework.data.repository.query.Param("id") Long id);
}
