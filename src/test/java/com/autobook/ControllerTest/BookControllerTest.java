package com.autobook.ControllerTest;

import com.autobook.Enum.PrivacyType;
import com.autobook.Library.Book.BookController;
import com.autobook.Library.Book.BookService;
import com.autobook.Library.Book.DTO.Request.CreateBookRequest;
import com.autobook.Library.Book.DTO.Request.UpdateBookRequest;
import com.autobook.Library.Book.DTO.Response.BookDetailsResponse;
import com.autobook.Social.User.User;
import com.autobook.Social.User.UserRepository;
import com.autobook.util.UserTestBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookControllerTest {

    @Mock
    private BookService bookService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookController bookController;

    private Principal createPrincipal(String name) {
        return () -> name;
    }

    @Test
    void createBook() {
        Principal p = createPrincipal("user");
        User user = new UserTestBuilder().withId(1L).withUsername("user").build();
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));

        CreateBookRequest req = new CreateBookRequest("Title", "Desc", "Genre", PrivacyType.PUBLIC, "img");
        BookDetailsResponse resp = mock(BookDetailsResponse.class);
        when(bookService.createBook(user, req)).thenReturn(resp);

        assertEquals(resp, bookController.createBook(req, p));
    }

    @Test
    void getBook() {
        BookDetailsResponse resp = mock(BookDetailsResponse.class);
        when(bookService.getBookById(1L)).thenReturn(resp);
        assertEquals(resp, bookController.getBook(1L));
    }

    @Test
    void getAllBooks() {
        when(bookService.getAllBooks()).thenReturn(List.of());
        assertEquals(0, bookController.getAllBooks().size());
    }

    @Test
    void getMyBooks() {
        Principal p = createPrincipal("user");
        User user = new UserTestBuilder().withId(1L).withUsername("user").build();
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(bookService.getBooksByAuthorOrdered(user)).thenReturn(List.of());
        assertEquals(0, bookController.getMyBooks(p).size());
    }

    @Test
    void getBooksByAuthor() {
        User user = new UserTestBuilder().withId(1L).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookService.getBooksByAuthorOrdered(user)).thenReturn(List.of());
        assertEquals(0, bookController.getBooksByAuthor(1L).size());
    }

    @Test
    void getBooksByPrivacy() {
        when(bookService.getBooksByPrivacy(PrivacyType.PUBLIC)).thenReturn(List.of());
        assertEquals(0, bookController.getBooksByPrivacy(PrivacyType.PUBLIC).size());
    }

    @Test
    void getBooksByAuthorAndPrivacy() {
        User user = new UserTestBuilder().withId(1L).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookService.getBooksByAuthorAndPrivacy(user, PrivacyType.PRIVATE)).thenReturn(List.of());
        assertEquals(0, bookController.getBooksByAuthorAndPrivacy(1L, PrivacyType.PRIVATE).size());
    }

    @Test
    void searchBooks() {
        when(bookService.searchBooksByTitle("term")).thenReturn(List.of());
        assertEquals(0, bookController.searchBooks("term").size());
    }

    @Test
    void getBooksByGenre() {
        when(bookService.getBooksByGenre("Fantasy")).thenReturn(List.of());
        assertEquals(0, bookController.getBooksByGenre("Fantasy").size());
    }

    @Test
    void updateBook() {
        UpdateBookRequest req = new UpdateBookRequest("T", "D", "G", PrivacyType.PUBLIC, "img");
        BookDetailsResponse resp = mock(BookDetailsResponse.class);
        when(bookService.updateBook(1L, req)).thenReturn(resp);
        assertEquals(resp, bookController.updateBook(1L, req));
    }

    @Test
    void deleteBook() {
        bookController.deleteBook(1L);
        verify(bookService).deleteBook(1L);
    }
}
