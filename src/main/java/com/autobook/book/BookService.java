package com.autobook.book;

import com.autobook.user.User;
import com.autobook.entity.PrivacyType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Service
@Transactional
public class BookService {
    private final BookRepository bookRepository;
    
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }
    
    public Book createBook(User author, String title, String description, String genre, PrivacyType privacy) {
        if (title == null || title.trim().isEmpty()) {
            throw new RuntimeException("Book title is required");
        }
        
        if (author == null) {
            throw new RuntimeException("Book must have an author");
        }
        
        Book book = new Book();
        book.setAuthor(author);
        book.setTitle(title);
        book.setDescription(description);
        book.setGenre(genre);
        book.setPrivacy(privacy != null ? privacy : PrivacyType.PRIVATE);
        book.setIsFeatured(false);
        
        return bookRepository.save(book);
    }
    
    public Book getBookById(Long bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
    }
    
    public Book updateBook(Long bookId, User editor, String title, String description, String genre, 
                          PrivacyType privacy, String coverImage) {
        Book book = getBookById(bookId);
        
        if (!book.getAuthor().getId().equals(editor.getId())) {
            throw new RuntimeException("You don't have permission to update this book");
        }
        
        if (title != null && !title.trim().isEmpty()) {
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
    
    public void deleteBook(Long bookId, User deleter) {
        Book book = getBookById(bookId);
        
        if (!book.getAuthor().getId().equals(deleter.getId())) {
            throw new RuntimeException("Only the author can delete this book");
        }
        
        bookRepository.delete(book);
    }
    
    public List<Book> getBooksByAuthor(User author) {
        return bookRepository.findByAuthorOrderByCreatedAtDesc(author);
    }
    
    public List<Book> getPublicBooks() {
        return bookRepository.findByPrivacy(PrivacyType.PUBLIC.name());
    }
    
    public List<Book> getFeaturedBooks() {
        return bookRepository.findByIsFeaturedTrueOrderByCreatedAtDesc();
    }
    
    public List<Book> searchBooksByTitle(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return List.of();
        }
        return bookRepository.findByTitleContainingIgnoreCase(searchTerm.trim());
    }
    
    public List<Book> getBooksByGenre(String genre) {
        return bookRepository.findByGenre(genre);
    }
    
    public Page<Book> getPublicBooksPaginated(Pageable pageable) {
        return bookRepository.findByPrivacyOrderByCreatedAtDesc(PrivacyType.PUBLIC.name(), pageable);
    }
    
    public Book toggleFeaturedStatus(Long bookId, boolean isFeatured) {
        Book book = getBookById(bookId);
        book.setIsFeatured(isFeatured);
        return bookRepository.save(book);
    }
    
    public Long getBookCountByAuthor(User author) {
        return bookRepository.countByAuthor(author);
    }
    
    public List<Book> getVisibleBooksForUser(User viewer, User targetAuthor) {
        List<Book> allBooks = bookRepository.findByAuthorOrderByCreatedAtDesc(targetAuthor);
        
        return allBooks.stream()
                .filter(book -> canUserViewBook(viewer, book))
                .toList();
    }
    
    public List<Book> getRecentPublicBooks(int limit) {
        Page<Book> recentPage = bookRepository.findByPrivacyOrderByCreatedAtDesc(
            PrivacyType.PUBLIC.name(), 
            Pageable.ofSize(limit)
        );
        return recentPage.getContent();
    }
    
    private boolean canUserViewBook(User viewer, Book book) {
        if (book.getPrivacy() == PrivacyType.PUBLIC) {
            return true;
        }
        
        if (book.getPrivacy() == PrivacyType.PRIVATE) {
            return viewer != null && viewer.getId().equals(book.getAuthor().getId());
        }
        
        return false;
    }
}