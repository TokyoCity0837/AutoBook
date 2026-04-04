package com.autobook.ServiceTest;

import com.autobook.Exception.ChapterNotFoundException;
import com.autobook.Exception.EmptyChapterTitleException;
import com.autobook.Factory.ChapterFactory;
import com.autobook.Library.Book.Book;
import com.autobook.Library.Chapter.Chapter;
import com.autobook.Library.Chapter.ChapterRepository;
import com.autobook.Library.Chapter.ChapterService;
import com.autobook.util.BookTestBuilder;
import com.autobook.util.ChapterTestBuilder;
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
public class ChapterServiceTest {

    @Mock
    private ChapterRepository chapterRepository;

    @Mock
    private ChapterFactory chapterFactory;

    @InjectMocks
    private ChapterService chapterService;

    @Test
    void createChapter_ok() {
        Book book = new BookTestBuilder().build();

        Chapter chapter = new ChapterTestBuilder()
                .withBook(book)
                .withTitle("Intro")
                .withContent("Hello world")
                .build();

        when(chapterFactory.create(book, "Intro", "Hello world")).thenReturn(chapter);
        when(chapterRepository.save(chapter)).thenReturn(chapter);

        Chapter result = chapterService.createChapter(book, "Intro", "Hello world");

        assertNotNull(result);
        assertEquals(book, result.getBook());
        assertEquals("Intro", result.getTitle());
        assertEquals("Hello world", result.getContent());

        verify(chapterFactory).create(book, "Intro", "Hello world");
        verify(chapterRepository).save(chapter);
    }

    @Test
    void createChapter_emptyTitle() {
        Book book = new BookTestBuilder().build();

        assertThrows(
                EmptyChapterTitleException.class,
                () -> chapterService.createChapter(book, " ", "Hello world")
        );

        verify(chapterFactory, never()).create(any(), any(), any());
        verify(chapterRepository, never()).save(any(Chapter.class));
    }

    @Test
    void createChapter_nullTitle() {
        Book book = new BookTestBuilder().build();

        assertThrows(
                EmptyChapterTitleException.class,
                () -> chapterService.createChapter(book, null, "Hello world")
        );

        verify(chapterFactory, never()).create(any(), any(), any());
        verify(chapterRepository, never()).save(any(Chapter.class));
    }

    @Test
    void getChapterById_ok() {
        Chapter chapter = new ChapterTestBuilder()
                .withId(1L)
                .withTitle("Intro")
                .build();

        when(chapterRepository.findById(1L)).thenReturn(Optional.of(chapter));

        Chapter result = chapterService.getChapterById(1L);

        assertEquals(1L, result.getId());
        assertEquals("Intro", result.getTitle());

        verify(chapterRepository).findById(1L);
    }

    @Test
    void getChapterById_notFound() {
        when(chapterRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
                ChapterNotFoundException.class,
                () -> chapterService.getChapterById(1L)
        );

        verify(chapterRepository).findById(1L);
    }

    @Test
    void getChaptersByBook_ok() {
        Book book = new BookTestBuilder().build();

        Chapter chapter1 = new ChapterTestBuilder()
                .withId(1L)
                .withBook(book)
                .withTitle("Intro")
                .build();

        Chapter chapter2 = new ChapterTestBuilder()
                .withId(2L)
                .withBook(book)
                .withTitle("Ending")
                .build();

        when(chapterRepository.findByBook(book)).thenReturn(List.of(chapter1, chapter2));

        List<Chapter> result = chapterService.getChaptersByBook(book);

        assertEquals(2, result.size());
        assertEquals("Intro", result.get(0).getTitle());
        assertEquals("Ending", result.get(1).getTitle());
        assertEquals(book, result.get(1).getBook());

        verify(chapterRepository).findByBook(book);
    }

    @Test
    void getChaptersByBookOrdered_ok() {
        Book book = new BookTestBuilder().build();

        Chapter chapter1 = new ChapterTestBuilder()
                .withId(1L)
                .withBook(book)
                .withTitle("Chapter 1")
                .build();

        Chapter chapter2 = new ChapterTestBuilder()
                .withId(2L)
                .withBook(book)
                .withTitle("Chapter 2")
                .build();

        when(chapterRepository.findByBookOrderByCreatedAtAsc(book))
                .thenReturn(List.of(chapter1, chapter2));

        List<Chapter> result = chapterService.getChaptersByBookOrdered(book);

        assertEquals(2, result.size());
        assertEquals("Chapter 1", result.get(0).getTitle());
        assertEquals("Chapter 2", result.get(1).getTitle());

        verify(chapterRepository).findByBookOrderByCreatedAtAsc(book);
    }

    @Test
    void countChaptersByBook_ok() {
        Book book = new BookTestBuilder().build();

        when(chapterRepository.countByBook(book)).thenReturn(3L);

        Long result = chapterService.countChaptersByBook(book);

        assertEquals(3L, result);
        verify(chapterRepository).countByBook(book);
    }

    @Test
    void searchChaptersByContent_ok() {
        Chapter chapter1 = new ChapterTestBuilder()
                .withId(1L)
                .withTitle("Intro")
                .withContent("Java basics")
                .build();

        Chapter chapter2 = new ChapterTestBuilder()
                .withId(2L)
                .withTitle("Advanced")
                .withContent("More Java")
                .build();

        when(chapterRepository.findByContentContainingIgnoreCase("java"))
                .thenReturn(List.of(chapter1, chapter2));

        List<Chapter> result = chapterService.searchChaptersByContent("java");

        assertEquals(2, result.size());
        assertEquals("Java basics", result.get(0).getContent());
        assertEquals("More Java", result.get(1).getContent());

        verify(chapterRepository).findByContentContainingIgnoreCase("java");
    }

    @Test
    void updateChapter_ok_updateTitleAndContent() {
        Chapter chapter = new ChapterTestBuilder()
                .withId(1L)
                .withTitle("Old title")
                .withContent("Old content")
                .build();

        when(chapterRepository.findById(1L)).thenReturn(Optional.of(chapter));
        when(chapterRepository.save(chapter)).thenReturn(chapter);

        Chapter result = chapterService.updateChapter(1L, "New title", "New content");

        assertEquals("New title", result.getTitle());
        assertEquals("New content", result.getContent());

        verify(chapterRepository).findById(1L);
        verify(chapterRepository).save(chapter);
    }

    @Test
    void updateChapter_ok_onlyTitle() {
        Chapter chapter = new ChapterTestBuilder()
                .withId(1L)
                .withTitle("Old title")
                .withContent("Old content")
                .build();

        when(chapterRepository.findById(1L)).thenReturn(Optional.of(chapter));
        when(chapterRepository.save(chapter)).thenReturn(chapter);

        Chapter result = chapterService.updateChapter(1L, "New title", null);

        assertEquals("New title", result.getTitle());
        assertEquals("Old content", result.getContent());

        verify(chapterRepository).findById(1L);
        verify(chapterRepository).save(chapter);
    }

    @Test
    void updateChapter_ok_onlyContent() {
        Chapter chapter = new ChapterTestBuilder()
                .withId(1L)
                .withTitle("Old title")
                .withContent("Old content")
                .build();

        when(chapterRepository.findById(1L)).thenReturn(Optional.of(chapter));
        when(chapterRepository.save(chapter)).thenReturn(chapter);

        Chapter result = chapterService.updateChapter(1L, null, "New content");

        assertEquals("Old title", result.getTitle());
        assertEquals("New content", result.getContent());

        verify(chapterRepository).findById(1L);
        verify(chapterRepository).save(chapter);
    }

    @Test
    void updateChapter_notFound() {
        when(chapterRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
                ChapterNotFoundException.class,
                () -> chapterService.updateChapter(1L, "New title", "New content")
        );

        verify(chapterRepository).findById(1L);
        verify(chapterRepository, never()).save(any(Chapter.class));
    }

    @Test
    void deleteChapter_ok() {
        Chapter chapter = new ChapterTestBuilder()
                .withId(1L)
                .build();

        when(chapterRepository.findById(1L)).thenReturn(Optional.of(chapter));

        chapterService.deleteChapter(1L);

        verify(chapterRepository).findById(1L);
        verify(chapterRepository).delete(chapter);
    }

    @Test
    void deleteChapter_notFound() {
        when(chapterRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
                ChapterNotFoundException.class,
                () -> chapterService.deleteChapter(1L)
        );

        verify(chapterRepository).findById(1L);
        verify(chapterRepository, never()).delete(any(Chapter.class));
    }
}