package com.autobook.social;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.autobook.book.Book;
import com.autobook.user.User;

public interface CommentRepository extends JpaRepository<Comment, Long> {


    List<Comment> findByBook(Book book);

    List<Comment> findByBookOrderByCreatedAtDesc(Book book);

    List<Comment> findByUser(User user);

    List<Comment> findByUserOrderByCreatedAtDesc(User user);

    Long countByBook(Book book);

    List<Comment> findByContentContainingIgnoreCase(String text);

    List<Comment> findByBookAndContentContainingIgnoreCase(Book book, String text);
}
