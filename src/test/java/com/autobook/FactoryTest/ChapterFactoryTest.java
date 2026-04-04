package com.autobook.FactoryTest;

import com.autobook.Factory.ChapterFactory;
import com.autobook.Library.Book.Book;
import com.autobook.Library.Chapter.Chapter;
import com.autobook.util.BookTestBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ChapterFactoryTest {

    private final ChapterFactory chapterFactory = new ChapterFactory();

    @Test
    void createChapter_ok() {
        Book book = new BookTestBuilder().build();

        Chapter result = chapterFactory.create(book, "Intro", "Hello world");

        assertNotNull(result);
        assertEquals(book, result.getBook());
        assertEquals("Intro", result.getTitle());
        assertEquals("Hello world", result.getContent());
    }

    @Test
    void createChapter_withNullContent_ok() {
        Book book = new BookTestBuilder().build();

        Chapter result = chapterFactory.create(book, "Intro", null);

        assertNotNull(result);
        assertEquals(book, result.getBook());
        assertEquals("Intro", result.getTitle());
        assertNull(result.getContent());
    }
}