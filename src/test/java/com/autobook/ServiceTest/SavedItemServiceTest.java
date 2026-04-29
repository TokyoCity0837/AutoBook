package com.autobook.ServiceTest;

import com.autobook.Library.Book.Book;
import com.autobook.Library.Book.BookMapper;
import com.autobook.Library.Book.BookRepository;
import com.autobook.Library.Chapter.Chapter;
import com.autobook.Library.Chapter.ChapterRepository;
import com.autobook.Library.SavedItem.SavedItem;
import com.autobook.Library.SavedItem.SavedItemRepository;
import com.autobook.Library.SavedItem.SavedItemService;
import com.autobook.Library.SavedItem.DTO.Response.SavedItemResponse;
import com.autobook.Social.User.User;
import com.autobook.util.UserTestBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SavedItemServiceTest {

    @Mock
    private SavedItemRepository savedItemRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private ChapterRepository chapterRepository;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private SavedItemService savedItemService;

    @Test
    void toggleSaveBook_saves_whenNotSaved() {
        User user = new UserTestBuilder().withId(1L).build();
        Book book = new Book();
        book.setId(10L);

        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));
        when(savedItemRepository.findByUserAndBook(user, book)).thenReturn(Optional.empty());

        savedItemService.toggleSaveBook(user, 10L);

        verify(savedItemRepository).save(any(SavedItem.class));
    }

    @Test
    void toggleSaveBook_deletes_whenAlreadySaved() {
        User user = new UserTestBuilder().withId(1L).build();
        Book book = new Book();
        book.setId(10L);
        SavedItem item = new SavedItem();

        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));
        when(savedItemRepository.findByUserAndBook(user, book)).thenReturn(Optional.of(item));

        savedItemService.toggleSaveBook(user, 10L);

        verify(savedItemRepository).delete(item);
    }

    @Test
    void toggleSaveChapter_saves_whenNotSaved() {
        User user = new UserTestBuilder().withId(1L).build();
        Chapter chapter = new Chapter();
        chapter.setId(10L);

        when(chapterRepository.findById(10L)).thenReturn(Optional.of(chapter));
        when(savedItemRepository.findByUserAndChapter(user, chapter)).thenReturn(Optional.empty());

        savedItemService.toggleSaveChapter(user, 10L);

        verify(savedItemRepository).save(any(SavedItem.class));
    }

    @Test
    void isBookSaved() {
        User user = new UserTestBuilder().withId(1L).build();
        Book book = new Book();
        book.setId(10L);

        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));
        when(savedItemRepository.existsByUserAndBook(user, book)).thenReturn(true);

        boolean result = savedItemService.isBookSaved(user, 10L);
        assertTrue(result);
    }

    @Test
    void getMySavedItems() {
        User user = new UserTestBuilder().withId(1L).build();
        SavedItem item = new SavedItem();
        item.setId(1L);
        item.setItemType(SavedItem.ItemType.BOOK);
        Book book = new Book();
        item.setBook(book);

        when(savedItemRepository.findByUserOrderBySavedAtDesc(user)).thenReturn(List.of(item));
        when(bookMapper.toCardResponse(book)).thenReturn(null);

        List<SavedItemResponse> items = savedItemService.getMySavedItems(user);

        assertEquals(1, items.size());
        assertEquals(1L, items.get(0).id());
    }
}
