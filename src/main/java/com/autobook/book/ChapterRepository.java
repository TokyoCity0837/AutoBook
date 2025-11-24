package com.autobook.book;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChapterRepository extends JpaRepository<Chapter, Long>{

    List<Chapter> findByBook(Book book);

    Long countByBook(Book book);

    List<Chapter> findByContentContainingIgnoreCase(String text);

}
