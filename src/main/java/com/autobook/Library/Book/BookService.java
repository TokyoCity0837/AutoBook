package com.autobook.Library.Book;

import com.autobook.Enum.PrivacyType;
import com.autobook.Exception.BookNotFoundException;
import com.autobook.Exception.EmptyBookTitleException;
import com.autobook.Factory.BookFactory;
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

    @Transactional
    public Book createBook(User author,
                           String title,
                           String description,
                           String genre,
                           PrivacyType privacy,
                           String coverImage) {
        validateTitle(title);

        Book book = bookFactory.create(author, title, description, genre, privacy, coverImage);
        return bookRepository.save(book);
    }

    public Book getBookById(Long bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public List<Book> getBooksByAuthor(User author) {
        return bookRepository.findByAuthor(author);
    }

    public List<Book> getBooksByPrivacy(PrivacyType privacy) {
        return bookRepository.findByPrivacy(privacy);
    }

    @Transactional
    public Book updateBook(Long bookId,
                           String title,
                           String description,
                           String genre,
                           PrivacyType privacy,
                           String coverImage) {
        Book book = getBookById(bookId);

        if (title != null) {
            validateTitle(title);
            book.setTitle(title);
        }

        if (description != null) {
            book.setDescription(description);
        }

        if (genre != null) {
            book.setGenre(genre);
        }

        if (privacy != null) {
            book.setPrivacy(privacy);
        }

        if (coverImage != null) {
            book.setCoverImage(coverImage);
        }

        return bookRepository.save(book);
    }

    @Transactional
    public void deleteBook(Long bookId) {
        Book book = getBookById(bookId);
        bookRepository.delete(book);
    }

    private void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new EmptyBookTitleException();
        }
    }

    public List<Book> getBooksByAuthorOrdered(User author) {
        return bookRepository.findByAuthorOrderByCreatedAtDesc(author);
    }

    public List<Book> getBooksByAuthorAndPrivacy(User author, PrivacyType privacy) {
        return bookRepository.findByAuthorAndPrivacyOrderByCreatedAtDesc(author, privacy);
    }

    public List<Book> searchBooksByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title);
    }

    public List<Book> getBooksByGenre(String genre) {
        return bookRepository.findByGenreOrderByCreatedAtDesc(genre);
    }
}