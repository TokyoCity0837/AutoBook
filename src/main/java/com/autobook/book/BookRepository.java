package com.autobook.book;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import com.autobook.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookRepository extends JpaRepository<Book, Long>{

    List<Book> findByAuthor(User author);

    List<Book> findByAuthorOrderByCreatedAtDesc(User author);

    List<Book> findByPrivacy(String privacy);

    List<Book> findByIsFeaturedTrue();

    Long countByAuthor(User author);

    List<Book> findByIsFeaturedTrueOrderByCreatedAtDesc();

    List<Book> findByTitleContainingIgnoreCase(String title);

    List<Book> findByGenre(String genre);
    
    Page<Book> findByPrivacyOrderByCreatedAtDesc(String privacy, Pageable pageable);
}
