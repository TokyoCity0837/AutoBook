package com.autobook.ServiceTest;

import com.autobook.Enum.PrivacyType;
import com.autobook.Exception.BookNotFoundException;
import com.autobook.Exception.EmptyBookTitleException;
import com.autobook.Factory.BookFactory;
import com.autobook.Social.User.User;
import com.autobook.Library.Book.*;
import com.autobook.util.BookTestBuilder;
import com.autobook.util.UserTestBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookFactory bookFactory;

    @InjectMocks
    private BookService bookService;

    @Test
    void createBook_ok() {
        User author = new UserTestBuilder().build();

        Book createdBook = new BookTestBuilder()
                .withAuthor(author)
                .withTitle("My Book")
                .withDescription("About book")
                .withGenre("Fantasy")
                .withPrivacy(PrivacyType.PUBLIC)
                .withCoverImage("cover.png")
                .build();

        when(bookFactory.create(
                author,
                "My Book",
                "About book",
                "Fantasy",
                PrivacyType.PUBLIC,
                "cover.png"
        )).thenReturn(createdBook);

        when(bookRepository.save(createdBook)).thenReturn(createdBook);

        Book result = bookService.createBook(
                author,
                "My Book",
                "About book",
                "Fantasy",
                PrivacyType.PUBLIC,
                "cover.png"
        );

        assertNotNull(result);
        assertEquals("My Book", result.getTitle());
        assertEquals("About book", result.getDescription());
        assertEquals("Fantasy", result.getGenre());
        assertEquals(PrivacyType.PUBLIC, result.getPrivacy());
        assertEquals("cover.png", result.getCoverImage());

        verify(bookRepository).save(createdBook);
    }

    @Test
    void createBook_emptyTitle() {
        User author = new UserTestBuilder().build();

        assertThrows(
                EmptyBookTitleException.class,
                () -> bookService.createBook(author, "   ", "desc", "Fantasy", PrivacyType.PUBLIC, "cover.png")
        );

        verify(bookFactory, never()).create(any(), any(), any(), any(), any(), any());
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void getBookById_ok() {
        Book book = new BookTestBuilder()
                .withId(1L)
                .withTitle("Book 1")
                .build();

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        Book result = bookService.getBookById(1L);

        assertEquals(1L, result.getId());
        assertEquals("Book 1", result.getTitle());
    }

    @Test
    void getBookById_notFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> bookService.getBookById(1L));
    }

    @Test
    void getAllBooks_ok() {
        Book book1 = new BookTestBuilder().withId(1L).withTitle("Book 1").build();
        Book book2 = new BookTestBuilder().withId(2L).withTitle("Book 2").build();

        when(bookRepository.findAll()).thenReturn(List.of(book1, book2));

        List<Book> result = bookService.getAllBooks();

        assertEquals(2, result.size());
        assertEquals("Book 1", result.get(0).getTitle());
        assertEquals("Book 2", result.get(1).getTitle());
    }

    @Test
    void getBooksByAuthor_ok() {
        User author = new UserTestBuilder().build();

        Book book1 = new BookTestBuilder().withAuthor(author).withTitle("Book 1").build();
        Book book2 = new BookTestBuilder().withAuthor(author).withTitle("Book 2").build();

        when(bookRepository.findByAuthor(author)).thenReturn(List.of(book1, book2));

        List<Book> result = bookService.getBooksByAuthor(author);

        assertEquals(2, result.size());
    }

    @Test
    void getBooksByPrivacy_ok() {
        Book book = new BookTestBuilder()
                .withPrivacy(PrivacyType.PUBLIC)
                .build();

        when(bookRepository.findByPrivacy(PrivacyType.PUBLIC)).thenReturn(List.of(book));

        List<Book> result = bookService.getBooksByPrivacy(PrivacyType.PUBLIC);

        assertEquals(1, result.size());
        assertEquals(PrivacyType.PUBLIC, result.get(0).getPrivacy());
    }

    @Test
    void updateBook_ok() {
        Book book = new BookTestBuilder()
                .withId(1L)
                .withTitle("Old Title")
                .withDescription("Old Description")
                .withGenre("Old Genre")
                .withPrivacy(PrivacyType.PRIVATE)
                .withCoverImage("old.png")
                .build();

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(book)).thenReturn(book);

        Book result = bookService.updateBook(
                1L,
                "New Title",
                "New Description",
                "Fantasy",
                PrivacyType.PUBLIC,
                "new.png"
        );

        assertEquals("New Title", result.getTitle());
        assertEquals("New Description", result.getDescription());
        assertEquals("Fantasy", result.getGenre());
        assertEquals(PrivacyType.PUBLIC, result.getPrivacy());
        assertEquals("new.png", result.getCoverImage());

        verify(bookRepository).save(book);
    }

    @Test
    void updateBook_onlyTitle() {
        Book book = new BookTestBuilder()
                .withId(1L)
                .withTitle("Old Title")
                .withDescription("Old Description")
                .build();

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(book)).thenReturn(book);

        Book result = bookService.updateBook(
                1L,
                "New Title",
                null,
                null,
                null,
                null
        );

        assertEquals("New Title", result.getTitle());
        assertEquals("Old Description", result.getDescription());

        verify(bookRepository).save(book);
    }

    @Test
    void updateBook_emptyTitle() {
        Book book = new BookTestBuilder()
                .withId(1L)
                .withTitle("Old Title")
                .build();

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        assertThrows(
                EmptyBookTitleException.class,
                () -> bookService.updateBook(1L, "   ", null, null, null, null)
        );

        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void deleteBook_ok() {
        Book book = new BookTestBuilder()
                .withId(1L)
                .build();

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        bookService.deleteBook(1L);

        verify(bookRepository).delete(book);
    }

    @Test
    void getBooksByAuthorOrdered_ok() {
        User author = new UserTestBuilder().build();
        Book book = new BookTestBuilder().withAuthor(author).build();

        when(bookRepository.findByAuthorOrderByCreatedAtDesc(author)).thenReturn(List.of(book));

        List<Book> result = bookService.getBooksByAuthorOrdered(author);

        assertEquals(1, result.size());
    }

    @Test
    void getBooksByAuthorAndPrivacy_ok() {
        User author = new UserTestBuilder().build();
        Book book = new BookTestBuilder()
                .withAuthor(author)
                .withPrivacy(PrivacyType.PUBLIC)
                .build();

        when(bookRepository.findByAuthorAndPrivacyOrderByCreatedAtDesc(author, PrivacyType.PUBLIC))
                .thenReturn(List.of(book));

        List<Book> result = bookService.getBooksByAuthorAndPrivacy(author, PrivacyType.PUBLIC);

        assertEquals(1, result.size());
        assertEquals(PrivacyType.PUBLIC, result.get(0).getPrivacy());
    }

    @Test
    void searchBooksByTitle_ok() {
        Book book = new BookTestBuilder()
                .withTitle("Harry Potter")
                .build();

        when(bookRepository.findByTitleContainingIgnoreCase("harry"))
                .thenReturn(List.of(book));

        List<Book> result = bookService.searchBooksByTitle("harry");

        assertEquals(1, result.size());
        assertEquals("Harry Potter", result.get(0).getTitle());
    }

    @Test
    void getBooksByGenre_ok() {
        Book book = new BookTestBuilder()
                .withGenre("Fantasy")
                .build();

        when(bookRepository.findByGenreOrderByCreatedAtDesc("Fantasy"))
                .thenReturn(List.of(book));

        List<Book> result = bookService.getBooksByGenre("Fantasy");

        assertEquals(1, result.size());
        assertEquals("Fantasy", result.get(0).getGenre());
    }
}