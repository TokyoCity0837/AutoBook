package com.autobook.FactoryTest;

import com.autobook.Enum.PrivacyType;
import com.autobook.Library.Book.Book;
import com.autobook.Factory.BookFactory;
import com.autobook.Social.User.User;
import com.autobook.util.UserTestBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BookFactoryTest {

    private final BookFactory bookFactory = new BookFactory();

    @Test
    void createBook_ok() {
        User author = new UserTestBuilder()
                .withUsername("anton")
                .build();

        Book book = bookFactory.create(
                author,
                "My Book",
                "Some description",
                "Fantasy",
                PrivacyType.PUBLIC,
                "cover.png"
        );

        assertNotNull(book);
        assertEquals(author, book.getAuthor());
        assertEquals("My Book", book.getTitle());
        assertEquals("Some description", book.getDescription());
        assertEquals("Fantasy", book.getGenre());
        assertEquals(PrivacyType.PUBLIC, book.getPrivacy());
        assertEquals("cover.png", book.getCoverImage());
    }

    @Test
    void createBook_defaultPrivacy() {
        User author = new UserTestBuilder().build();

        Book book = bookFactory.create(
                author,
                "Book",
                "Description",
                "Drama",
                null,
                "cover.png"
        );

        assertEquals(PrivacyType.PRIVATE, book.getPrivacy());
    }
}