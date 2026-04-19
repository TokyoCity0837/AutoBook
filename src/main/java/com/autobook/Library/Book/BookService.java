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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;
    private final BookFactory bookFactory;
    private final BookMapper bookMapper;

    private final EditRepository editRepository;
    private final EditMapper editMapper;

    @Transactional
    public BookDetailsResponse createBook(User author, CreateBookRequest request) {
        validateTitle(request.title());

        Book book = bookFactory.create(
                author,
                request.title(),
                request.description(),
                request.genre(),
                request.privacy(),
                request.coverImage()
        );

        Book savedBook = bookRepository.save(book);
        return buildBookDetailsResponse(savedBook);
    }

    public BookDetailsResponse getBookById(Long bookId) {
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

    @Transactional
    public BookDetailsResponse updateBook(Long bookId, UpdateBookRequest request) {
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
        
        if (request.font() != null) {
            book.setFont(request.font());
        }
        if (request.fontSize() != null) {
            book.setFontSize(request.fontSize());
        }
        if (request.lineHeight() != null) {
            book.setLineHeight(request.lineHeight());
        }
        if (request.paraStyle() != null) {
            book.setParaStyle(request.paraStyle());
        }

        Book savedBook = bookRepository.save(book);
        return buildBookDetailsResponse(savedBook);
    }

    @Transactional
    public void deleteBook(Long bookId) {
        Book book = findBookById(bookId);
        bookRepository.delete(book);
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
                .orElseThrow(() -> new BookNotFoundException(bookId));
    }

    private void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new EmptyBookTitleException();
        }
    }
}