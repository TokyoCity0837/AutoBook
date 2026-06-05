package com.autobook.ServiceTest;

import com.autobook.Enum.PrivacyType;
import com.autobook.Exception.BookNotFoundException;
import com.autobook.Exception.EmptyBookTitleException;
import com.autobook.Factory.BookFactory;
import com.autobook.Library.Book.Book;
import com.autobook.Library.Book.BookMapper;
import com.autobook.Library.Book.BookRepository;
import com.autobook.Library.Book.BookService;
import com.autobook.Library.Book.DTO.Request.CreateBookRequest;
import com.autobook.Library.Book.DTO.Request.UpdateBookRequest;
import com.autobook.Library.Book.DTO.Response.BookCardResponse;
import com.autobook.Library.Book.DTO.Response.BookDetailsResponse;
import com.autobook.Library.Edit.EditMapper;
import com.autobook.Library.Edit.EditRepository;
import com.autobook.Social.User.User;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

        @Mock
        private BookRepository bookRepository;

        @Mock
        private BookFactory bookFactory;

        @Mock
        private BookMapper bookMapper;

        @Mock
        private EditRepository editRepository;

        @Mock
        private EditMapper editMapper;

        @InjectMocks
        private BookService bookService;

        @Test
        void createBook_ok() {
                User author = new UserTestBuilder().build();

                CreateBookRequest request = new CreateBookRequest(
                                "My Book",
                                "About book",
                                "Fantasy",
                                PrivacyType.PUBLIC,
                                "cover.png");

                Book createdBook = new BookTestBuilder()
                                .withAuthor(author)
                                .withTitle("My Book")
                                .withDescription("About book")
                                .withGenre("Fantasy")
                                .withPrivacy(PrivacyType.PUBLIC)
                                .withCoverImage("cover.png")
                                .build();

                Book savedBook = new BookTestBuilder()
                                .withId(1L)
                                .withAuthor(author)
                                .withTitle("My Book")
                                .withDescription("About book")
                                .withGenre("Fantasy")
                                .withPrivacy(PrivacyType.PUBLIC)
                                .withCoverImage("cover.png")
                                .build();

                BookDetailsResponse expectedResponse = mock(BookDetailsResponse.class);

                when(bookFactory.create(
                                author,
                                request.title(),
                                request.description(),
                                request.genre(),
                                request.privacy(),
                                request.coverImage())).thenReturn(createdBook);

                when(bookRepository.save(createdBook)).thenReturn(savedBook);
                when(editRepository.findByBook(savedBook)).thenReturn(List.of());
                when(bookMapper.toDetailsResponse(savedBook, List.of())).thenReturn(expectedResponse);

                BookDetailsResponse result = bookService.createBook(author, request);

                assertNotNull(result);
                assertEquals(expectedResponse, result);

                verify(bookFactory).create(
                                author,
                                request.title(),
                                request.description(),
                                request.genre(),
                                request.privacy(),
                                request.coverImage());
                verify(bookRepository).save(createdBook);
                verify(editRepository).findByBook(savedBook);
                verify(bookMapper).toDetailsResponse(savedBook, List.of());
        }

        @Test
        void createBook_emptyTitle() {
                User author = new UserTestBuilder().build();

                CreateBookRequest request = new CreateBookRequest(
                                "   ",
                                "desc",
                                "Fantasy",
                                PrivacyType.PUBLIC,
                                "cover.png");

                assertThrows(
                                EmptyBookTitleException.class,
                                () -> bookService.createBook(author, request));

                verify(bookFactory, never()).create(any(), any(), any(), any(), any(), any());
                verify(bookRepository, never()).save(any(Book.class));
        }

        @Test
        void getBookById_ok() {
                Book book = new BookTestBuilder()
                                .withId(1L)
                                .withTitle("Book 1")
                                .build();

                BookDetailsResponse expectedResponse = mock(BookDetailsResponse.class);

                when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
                when(editRepository.findByBook(book)).thenReturn(List.of());
                when(bookMapper.toDetailsResponse(book, List.of())).thenReturn(expectedResponse);

                BookDetailsResponse result = bookService.getBookById(1L);

                assertNotNull(result);
                assertEquals(expectedResponse, result);

                verify(bookRepository).findById(1L);
                verify(editRepository).findByBook(book);
                verify(bookMapper).toDetailsResponse(book, List.of());
        }

        @Test
        void getBookById_notFound() {
                when(bookRepository.findById(1L)).thenReturn(Optional.empty());

                assertThrows(BookNotFoundException.class, () -> bookService.getBookById(1L));

                verify(bookRepository).findById(1L);
                verify(editRepository, never()).findByBook(any());
                verify(bookMapper, never()).toDetailsResponse(any(), any());
        }

        @Test
        void getAllBooks_ok() {
                Book book1 = new BookTestBuilder().withId(1L).withTitle("Book 1").build();
                Book book2 = new BookTestBuilder().withId(2L).withTitle("Book 2").build();

                BookCardResponse response1 = mock(BookCardResponse.class);
                BookCardResponse response2 = mock(BookCardResponse.class);

                when(bookRepository.findAll()).thenReturn(List.of(book1, book2));
                when(bookMapper.toCardResponse(book1)).thenReturn(response1);
                when(bookMapper.toCardResponse(book2)).thenReturn(response2);

                List<BookCardResponse> result = bookService.getAllBooks();

                assertEquals(2, result.size());
                assertEquals(response1, result.get(0));
                assertEquals(response2, result.get(1));

                verify(bookRepository).findAll();
                verify(bookMapper).toCardResponse(book1);
                verify(bookMapper).toCardResponse(book2);
        }

        @Test
        void getBooksByAuthor_ok() {
                User author = new UserTestBuilder().build();

                Book book1 = new BookTestBuilder().withAuthor(author).withTitle("Book 1").build();
                Book book2 = new BookTestBuilder().withAuthor(author).withTitle("Book 2").build();

                BookCardResponse response1 = mock(BookCardResponse.class);
                BookCardResponse response2 = mock(BookCardResponse.class);

                when(bookRepository.findByAuthor(author)).thenReturn(List.of(book1, book2));
                when(bookMapper.toCardResponse(book1)).thenReturn(response1);
                when(bookMapper.toCardResponse(book2)).thenReturn(response2);

                List<BookCardResponse> result = bookService.getBooksByAuthor(author);

                assertEquals(2, result.size());
                assertEquals(response1, result.get(0));
                assertEquals(response2, result.get(1));
        }

        @Test
        void getBooksByPrivacy_ok() {
                Book book = new BookTestBuilder()
                                .withPrivacy(PrivacyType.PUBLIC)
                                .build();

                BookCardResponse response = mock(BookCardResponse.class);

                when(bookRepository.findByPrivacy(PrivacyType.PUBLIC)).thenReturn(List.of(book));
                when(bookMapper.toCardResponse(book)).thenReturn(response);

                List<BookCardResponse> result = bookService.getBooksByPrivacy(PrivacyType.PUBLIC);

                assertEquals(1, result.size());
                assertEquals(response, result.get(0));
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

                UpdateBookRequest request = new UpdateBookRequest(
                                "New Title",
                                "New Description",
                                "Fantasy",
                                PrivacyType.PUBLIC,
                                "new.png");

                BookDetailsResponse expectedResponse = mock(BookDetailsResponse.class);

                when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
                when(bookRepository.save(book)).thenReturn(book);
                when(editRepository.findByBook(book)).thenReturn(List.of());
                when(bookMapper.toDetailsResponse(book, List.of())).thenReturn(expectedResponse);

                BookDetailsResponse result = bookService.updateBook(1L, request);

                assertNotNull(result);
                assertEquals(expectedResponse, result);

                assertEquals("New Title", book.getTitle());
                assertEquals("New Description", book.getDescription());
                assertEquals("Fantasy", book.getGenre());
                assertEquals(PrivacyType.PUBLIC, book.getPrivacy());
                assertEquals("new.png", book.getCoverImage());

                verify(bookRepository).save(book);
                verify(editRepository).findByBook(book);
                verify(bookMapper).toDetailsResponse(book, List.of());
        }

        @Test
        void updateBook_onlyTitle() {
                Book book = new BookTestBuilder()
                                .withId(1L)
                                .withTitle("Old Title")
                                .withDescription("Old Description")
                                .build();

                UpdateBookRequest request = new UpdateBookRequest(
                                "New Title",
                                null,
                                null,
                                null,
                                null);

                BookDetailsResponse expectedResponse = mock(BookDetailsResponse.class);

                when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
                when(bookRepository.save(book)).thenReturn(book);
                when(editRepository.findByBook(book)).thenReturn(List.of());
                when(bookMapper.toDetailsResponse(book, List.of())).thenReturn(expectedResponse);

                BookDetailsResponse result = bookService.updateBook(1L, request);

                assertNotNull(result);
                assertEquals(expectedResponse, result);

                assertEquals("New Title", book.getTitle());
                assertEquals("Old Description", book.getDescription());

                verify(bookRepository).save(book);
                verify(editRepository).findByBook(book);
                verify(bookMapper).toDetailsResponse(book, List.of());
        }

        @Test
        void updateBook_emptyTitle() {
                Book book = new BookTestBuilder()
                                .withId(1L)
                                .withTitle("Old Title")
                                .build();

                UpdateBookRequest request = new UpdateBookRequest(
                                "   ",
                                null,
                                null,
                                null,
                                null);

                when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

                assertThrows(
                                EmptyBookTitleException.class,
                                () -> bookService.updateBook(1L, request));

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

                BookCardResponse response = mock(BookCardResponse.class);

                when(bookRepository.findByAuthorOrderByCreatedAtDesc(author)).thenReturn(List.of(book));
                when(bookMapper.toCardResponse(book)).thenReturn(response);

                List<BookCardResponse> result = bookService.getBooksByAuthorOrdered(author);

                assertEquals(1, result.size());
                assertEquals(response, result.get(0));
        }

        @Test
        void getBooksByAuthorAndPrivacy_ok() {
                User author = new UserTestBuilder().build();
                Book book = new BookTestBuilder()
                                .withAuthor(author)
                                .withPrivacy(PrivacyType.PUBLIC)
                                .build();

                BookCardResponse response = mock(BookCardResponse.class);

                when(bookRepository.findByAuthorAndPrivacyOrderByCreatedAtDesc(author, PrivacyType.PUBLIC))
                                .thenReturn(List.of(book));
                when(bookMapper.toCardResponse(book)).thenReturn(response);

                List<BookCardResponse> result = bookService.getBooksByAuthorAndPrivacy(author, PrivacyType.PUBLIC);

                assertEquals(1, result.size());
                assertEquals(response, result.get(0));
        }

        @Test
        void searchBooksByTitle_ok() {
                Book book = new BookTestBuilder()
                                .withTitle("Harry Potter")
                                .build();

                BookCardResponse response = mock(BookCardResponse.class);

                when(bookRepository.findByTitleContainingIgnoreCase("harry"))
                                .thenReturn(List.of(book));
                when(bookMapper.toCardResponse(book)).thenReturn(response);

                List<BookCardResponse> result = bookService.searchBooksByTitle("harry");

                assertEquals(1, result.size());
                assertEquals(response, result.get(0));
        }

        @Test
        void getBooksByGenre_ok() {
                Book book = new BookTestBuilder()
                                .withGenre("Fantasy")
                                .build();

                BookCardResponse response = mock(BookCardResponse.class);

                when(bookRepository.findByGenreOrderByCreatedAtDesc("Fantasy"))
                                .thenReturn(List.of(book));
                when(bookMapper.toCardResponse(book)).thenReturn(response);

                List<BookCardResponse> result = bookService.getBooksByGenre("Fantasy");

                assertEquals(1, result.size());
                assertEquals(response, result.get(0));
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
