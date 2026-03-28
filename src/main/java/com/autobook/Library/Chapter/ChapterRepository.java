package com.autobook.Library.Chapter;

import org.springframework.data.jpa.repository.JpaRepository;
import com.autobook.Library.Book.Book;

import java.util.List;

public interface ChapterRepository extends JpaRepository<Chapter, Long> {

    List<Chapter> findByBook(Book book);

    List<Chapter> findByBookOrderByCreatedAtAsc(Book book);

    Long countByBook(Book book);

    List<Chapter> findByContentContainingIgnoreCase(String text);
}