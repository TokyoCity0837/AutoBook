package com.autobook.Library.Book;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.autobook.Social.User.User;
import com.autobook.Enum.PrivacyType;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findByAuthor(User author);

    List<Book> findByAuthorOrderByCreatedAtDesc(User author);

    List<Book> findByPrivacy(PrivacyType privacy);

    List<Book> findByPrivacyOrderByCreatedAtDesc(PrivacyType privacy);

    List<Book> findByAuthorAndPrivacy(User author, PrivacyType privacy);

    List<Book> findByAuthorAndPrivacyOrderByCreatedAtDesc(User author, PrivacyType privacy);

    Long countByAuthor(User author);

    List<Book> findByTitleContainingIgnoreCase(String title);

    List<Book> findByGenre(String genre);

    List<Book> findByGenreOrderByCreatedAtDesc(String genre);

    List<Book> findByAuthorAndGenre(User author, String genre);

    List<Book> findByAuthorAndGenreOrderByCreatedAtDesc(User author, String genre);

    Page<Book> findByAuthorAndPrivacyOrderByCreatedAtDesc(User author, PrivacyType privacy, Pageable pageable);

    Page<Book> findByPrivacyOrderByCreatedAtDesc(PrivacyType privacy, Pageable pageable);
}