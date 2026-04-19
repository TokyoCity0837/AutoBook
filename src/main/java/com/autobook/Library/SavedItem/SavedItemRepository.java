package com.autobook.Library.SavedItem;

import com.autobook.Social.User.User;
import com.autobook.Library.Book.Book;
import com.autobook.Library.Chapter.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SavedItemRepository extends JpaRepository<SavedItem, Long> {
    List<SavedItem> findByUserOrderBySavedAtDesc(User user);
    Optional<SavedItem> findByUserAndBook(User user, Book book);
    Optional<SavedItem> findByUserAndChapter(User user, Chapter chapter);
    boolean existsByUserAndBook(User user, Book book);
    boolean existsByUserAndChapter(User user, Chapter chapter);
}
