package com.autobook.Library.SavedItem;

import com.autobook.Exception.BookNotFoundException;
import com.autobook.Library.Book.Book;
import com.autobook.Library.Book.BookMapper;
import com.autobook.Library.Book.BookRepository;
import com.autobook.Library.Chapter.Chapter;
import com.autobook.Library.Chapter.ChapterRepository;
import com.autobook.Social.User.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SavedItemService {

    private final SavedItemRepository savedItemRepository;
    private final BookRepository bookRepository;
    private final ChapterRepository chapterRepository;
    private final BookMapper bookMapper;

    @Transactional
    public void toggleSaveBook(User user, Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));
        
        savedItemRepository.findByUserAndBook(user, book).ifPresentOrElse(
                savedItemRepository::delete,
                () -> {
                    SavedItem item = new SavedItem();
                    item.setUser(user);
                    item.setItemType(SavedItem.ItemType.BOOK);
                    item.setBook(book);
                    savedItemRepository.save(item);
                }
        );
    }

    @Transactional
    public void toggleSaveChapter(User user, Long chapterId) {
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new RuntimeException("Chapter not found"));

        savedItemRepository.findByUserAndChapter(user, chapter).ifPresentOrElse(
                savedItemRepository::delete,
                () -> {
                    SavedItem item = new SavedItem();
                    item.setUser(user);
                    item.setItemType(SavedItem.ItemType.CHAPTER);
                    item.setChapter(chapter);
                    savedItemRepository.save(item);
                }
        );
    }

    public boolean isBookSaved(User user, Long bookId) {
        Book book = bookRepository.findById(bookId).orElse(null);
        if (book == null) return false;
        return savedItemRepository.existsByUserAndBook(user, book);
    }

    public List<SavedItemResponse> getMySavedItems(User user) {
        return savedItemRepository.findByUserOrderBySavedAtDesc(user)
                .stream()
                .map(item -> new SavedItemResponse(
                        item.getId(),
                        item.getItemType(),
                        item.getBook() != null ? bookMapper.toCardResponse(item.getBook()) : null,
                        item.getSavedAt()
                ))
                .toList();
    }
}
