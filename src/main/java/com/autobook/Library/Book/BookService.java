package com.autobook.Library.Book;

import com.autobook.Enum.PrivacyType;
import com.autobook.Exception.BookNotFoundException;
import com.autobook.Exception.EmptyBookTitleException;
import com.autobook.Factory.BookFactory;
import com.autobook.Library.Book.DTO.Request.CreateBookRequest;
import com.autobook.Library.Book.DTO.Request.UpdateBookRequest;
import com.autobook.Library.Book.DTO.Response.BookCardResponse;
import com.autobook.Library.Book.DTO.Response.BookDetailsResponse;
import com.autobook.Library.Edit.EditRepository;
import com.autobook.Library.Edit.EditMapper;
import com.autobook.Library.Edit.DTO.Response.EditResponse;
import com.autobook.Social.User.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service class responsible for managing {@link Book} entities.
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;
    private final BookFactory bookFactory;
    private final BookMapper bookMapper;

    private final EditRepository editRepository;
    private final EditMapper editMapper;

    /**
     * Creates a new book using the provided author and request details.
     *
     * @param author  the user creating the book
     * @param request the request containing book details
     * @return a detailed response of the created book
     * @throws EmptyBookTitleException if the title is empty
     */
    @Transactional
    public BookDetailsResponse createBook(User author, CreateBookRequest request) {
        log.info("Creating new book for author: {}", author.getUsername());
        validateTitle(request.title());

        Book book = bookFactory.create(
                author,
                request.title(),
                request.description(),
                request.genre(),
                request.privacy(),
                request.coverImage());

        Book savedBook = bookRepository.save(book);
        log.debug("Book successfully saved with ID: {}", savedBook.getId());

        return buildBookDetailsResponse(savedBook);
    }

    /**
     * Retrieves a book by its unique identifier.
     *
     * @param bookId the unique identifier of the book
     * @return detailed response of the requested book
     * @throws BookNotFoundException if the book is not found in the repository
     */
    public BookDetailsResponse getBookById(Long bookId) {
        log.debug("Retrieving book by ID: {}", bookId);
        Book book = findBookById(bookId);
        return buildBookDetailsResponse(book);
    }

    public List<BookCardResponse> getAllBooks() {
        return bookRepository.findAll()
                .stream()
                .map(bookMapper::toCardResponse)
                .toList();
    }

    public List<BookCardResponse> getBooksByAuthor(User author) {
        return bookRepository.findByAuthor(author)
                .stream()
                .map(bookMapper::toCardResponse)
                .toList();
    }

    public List<BookCardResponse> getBooksByPrivacy(PrivacyType privacy) {
        return bookRepository.findByPrivacy(privacy)
                .stream()
                .map(bookMapper::toCardResponse)
                .toList();
    }

    /**
     * Updates an existing book. Only properties present in the request will be
     * modified.
     *
     * @param bookId  the unique identifier of the book to update
     * @param request the updated attributes for the book
     * @return a detailed response of the updated book
     * @throws BookNotFoundException if the book is not found
     */
    @Transactional
    public BookDetailsResponse updateBook(Long bookId, UpdateBookRequest request) {
        log.info("Updating book with ID: {}", bookId);
        Book book = findBookById(bookId);

        if (request.title() != null) {
            validateTitle(request.title());
            book.setTitle(request.title());
        }

        if (request.description() != null) {
            book.setDescription(request.description());
        }

        if (request.genre() != null) {
            book.setGenre(request.genre());
        }

        if (request.privacy() != null) {
            book.setPrivacy(request.privacy());
        }

        if (request.coverImage() != null) {
            book.setCoverImage(request.coverImage());
        }

        Book savedBook = bookRepository.save(book);
        log.debug("Book updated successfully: {}", savedBook.getTitle());
        return buildBookDetailsResponse(savedBook);
    }

    /**
     * Deletes a book by its identifier.
     *
     * @param bookId the unique identifier of the book to delete
     * @throws BookNotFoundException if the book is not found
     */
    @Transactional
    public void deleteBook(Long bookId) {
        log.info("Attempting to delete book with ID: {}", bookId);
        Book book = findBookById(bookId);
        bookRepository.delete(book);
        log.debug("Book deleted successfully");
    }

    public List<BookCardResponse> getBooksByAuthorOrdered(User author) {
        return bookRepository.findByAuthorOrderByCreatedAtDesc(author)
                .stream()
                .map(bookMapper::toCardResponse)
                .toList();
    }

    public List<BookCardResponse> getBooksByAuthorAndPrivacy(User author, PrivacyType privacy) {
        return bookRepository.findByAuthorAndPrivacyOrderByCreatedAtDesc(author, privacy)
                .stream()
                .map(bookMapper::toCardResponse)
                .toList();
    }

    public List<BookCardResponse> searchBooksByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title)
                .stream()
                .map(bookMapper::toCardResponse)
                .toList();
    }

    public List<BookCardResponse> getBooksByGenre(String genre) {
        return bookRepository.findByGenreOrderByCreatedAtDesc(genre)
                .stream()
                .map(bookMapper::toCardResponse)
                .toList();
    }

    private BookDetailsResponse buildBookDetailsResponse(Book book) {
        List<EditResponse> editRequests = editRepository.findByBook(book)
                .stream()
                .map(editMapper::toResponse)
                .toList();

        return bookMapper.toDetailsResponse(book, editRequests);
    }

    private Book findBookById(Long bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> {
                    log.error("Book not found for ID: {}", bookId);
                    return new BookNotFoundException(bookId);
                });
    }

    private void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            log.error("Attempted to set an empty title for a book");
            throw new EmptyBookTitleException();
        }
    }
}