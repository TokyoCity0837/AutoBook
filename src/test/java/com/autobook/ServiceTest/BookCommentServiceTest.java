package com.autobook.ServiceTest;

import com.autobook.Enum.PrivacyType;
import com.autobook.Exception.EmptyCommentContentException;
import com.autobook.Library.Book.Book;
import com.autobook.Library.Book.BookMapper;
import com.autobook.Library.Book.BookRepository;
import com.autobook.Library.Book.BookService;
import com.autobook.Library.BookComment.BookComment;
import com.autobook.Library.BookComment.BookCommentMapper;
import com.autobook.Library.BookComment.BookCommentRepository;
import com.autobook.Library.BookComment.BookCommentService;
import com.autobook.Library.BookComment.DTO.Request.CreateBookCommentRequest;
import com.autobook.Library.BookComment.DTO.Response.BookCommentResponse;
import com.autobook.Social.User.User;
import com.autobook.util.UserTestBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookCommentServiceTest {

    @Mock
    private BookCommentRepository bookCommentRepository;

    @Mock
    private BookCommentMapper bookCommentMapper;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookService bookService;

    @InjectMocks
    private BookCommentService bookCommentService;

    @Test
    void createComment_ok() {
        User author = new UserTestBuilder().withId(1L).withUsername("author").build();
        Book book = new Book();
        book.setId(10L);

        CreateBookCommentRequest request = new CreateBookCommentRequest("My comment", null);

        BookComment savedComment = new BookComment();
        savedComment.setId(100L);
        savedComment.setContent("My comment");

        BookCommentResponse response = new BookCommentResponse(
                100L, "My comment", null, LocalDateTime.now(), null, new java.util.ArrayList<>(), 0);

        when(bookCommentRepository.save(any(BookComment.class))).thenReturn(savedComment);
        when(bookCommentMapper.toResponse(savedComment)).thenReturn(response);

        BookCommentResponse result = bookCommentService.createComment(request, author, book);

        assertNotNull(result);
        assertEquals("My comment", result.content());
        verify(bookCommentRepository).save(any());
    }

    @Test
    void createComment_throwEmpty() {
        User author = new UserTestBuilder().withId(1L).withUsername("author").build();
        Book book = new Book();

        CreateBookCommentRequest request = new CreateBookCommentRequest("", null);

        assertThrows(EmptyCommentContentException.class,
                () -> bookCommentService.createComment(request, author, book));
    }

    @Test
    void getCommentsByBook() {
        Book book = new Book();
        book.setId(10L);

        BookComment comment = new BookComment();
        comment.setId(1L);
        comment.setCreatedAt(LocalDateTime.now());

        BookCommentResponse response = new BookCommentResponse(
                1L, "Content", null, LocalDateTime.now(), null, new java.util.ArrayList<>(), 0);

        when(bookCommentRepository.findByBookOrderByCreatedAtDesc(book)).thenReturn(List.of(comment));
        when(bookCommentMapper.toResponseLevel(eq(comment), any())).thenReturn(response);

        List<BookCommentResponse> result = bookCommentService.getCommentsByBook(book);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).id());
    }

    @Test
    void deleteCommentByIdAndAuthor_ok() {
        User author = new UserTestBuilder().withId(1L).build();
        BookComment comment = new BookComment();
        comment.setId(10L);
        comment.setAuthor(author);

        when(bookCommentRepository.findById(10L)).thenReturn(Optional.of(comment));

        bookCommentService.deleteCommentByIdAndAuthor(10L, author);

        verify(bookCommentRepository).delete(comment);
    }

    @Test
    void incrementLikeCount() {
        BookComment comment = new BookComment();
        when(bookCommentRepository.findById(10L)).thenReturn(Optional.of(comment));

        bookCommentService.incrementLikeCount(10L);

        verify(bookCommentRepository).incrementLikeCount(10L);
    }

    @Test
    void getAllBooks_Ok() {
        Book book = new Book();
        when(bookRepository.findAll()).thenReturn(List.of(book));
        assertEquals(1, bookService.getAllBooks().size());
    }

    @Test
    void getBooksByAuthor_Ok() {
        User author = new UserTestBuilder().withId(1L).build();
        Book book = new Book();
        when(bookRepository.findByAuthor(author)).thenReturn(List.of(book));
        assertEquals(1, bookService.getBooksByAuthor(author).size());
    }

    @Test
    void getBooksByPrivacy_Ok() {
        Book book = new Book();
        when(bookRepository.findByPrivacy(PrivacyType.PUBLIC)).thenReturn(List.of(book));
        assertEquals(1, bookService.getBooksByPrivacy(PrivacyType.PUBLIC).size());
    }

    @Test
    void getBooksByAuthorOrdered_Ok() {
        User author = new UserTestBuilder().withId(1L).build();
        Book book = new Book();
        when(bookRepository.findByAuthorOrderByCreatedAtDesc(author)).thenReturn(List.of(book));
        assertEquals(1, bookService.getBooksByAuthorOrdered(author).size());
    }

    @Test
    void getBooksByAuthorAndPrivacy_Ok() {
        User author = new UserTestBuilder().withId(1L).build();
        Book book = new Book();
        when(bookRepository.findByAuthorAndPrivacyOrderByCreatedAtDesc(author, PrivacyType.PUBLIC))
                .thenReturn(List.of(book));
        assertEquals(1, bookService.getBooksByAuthorAndPrivacy(author, PrivacyType.PUBLIC).size());
    }

    @Test
    void searchBooksByTitle_Ok() {
        Book book = new Book();
        when(bookRepository.findByTitleContainingIgnoreCase("test")).thenReturn(List.of(book));
        assertEquals(1, bookService.searchBooksByTitle("test").size());
    }

    @Test
    void getBooksByGenre_Ok() {
        Book book = new Book();
        when(bookRepository.findByGenreOrderByCreatedAtDesc("Fantasy")).thenReturn(List.of(book));
        assertEquals(1, bookService.getBooksByGenre("Fantasy").size());
    }
}
