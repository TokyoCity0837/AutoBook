package com.autobook.book;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface VocabularyRepository extends JpaRepository<Vocabulary, Long>{

    List<Vocabulary> findByBook(Book book);

    List<Vocabulary> findByBookOrderByWordAsc(Book book);

    List<Vocabulary> findByWordContainingIgnoreCase(String text);

    Optional<Vocabulary> findByBookAndWordIgnoreCase(Book book, String word);

    Long countByBook(Book book);
}
