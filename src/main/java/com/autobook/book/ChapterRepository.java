package com.autobook.book;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface ChapterRepository extends JpaRepository<Chapter, Long>{

    List<Chapter> findByBook(Book book);

    List<Chapter> findByBookOrderByOrderIndex(Book book);

    Optional<Chapter> findByBookAndOrderIndex(Book book, Integer OrderIndex);

    Optional<Chapter> findFirstByBookOrderByOrderIndexDesc(Book book);

    Long countByBook(Book book);

    List<Chapter> findByContentContainingIgnoreCase(String text);

}
