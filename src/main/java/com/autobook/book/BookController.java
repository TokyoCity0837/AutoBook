package com.autobook.book;

import com.autobook.user.User;
import com.autobook.user.UserService;
import com.autobook.entity.PrivacyType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {
    private final BookService bookService;
    private final UserService userService;
    
    public BookController(BookService bookService, UserService userService) {
        this.bookService = bookService;
        this.userService = userService;
    }
    
    @PostMapping
    public ResponseEntity<Book> createBook(
            @RequestParam Long authorId,
            @RequestBody BookRequest request) {
        User author = userService.getUserById(authorId);
        Book book = bookService.createBook(
            author,
            request.getTitle(),
            request.getDescription(),
            request.getGenre(),
            request.getPrivacy()
        );
        return ResponseEntity.ok(book);
    }
    
    @GetMapping("/{bookId}")
    public ResponseEntity<Book> getBookById(@PathVariable Long bookId) {
        Book book = bookService.getBookById(bookId);
        return ResponseEntity.ok(book);
    }
    
    @PutMapping("/{bookId}")
    public ResponseEntity<Book> updateBook(
            @PathVariable Long bookId,
            @RequestParam Long editorId,
            @RequestBody BookRequest request) {
        User editor = userService.getUserById(editorId);
        Book book = bookService.updateBook(
            bookId,
            editor,
            request.getTitle(),
            request.getDescription(),
            request.getGenre(),
            request.getPrivacy(),
            request.getCoverImage()
        );
        return ResponseEntity.ok(book);
    }
    
    @DeleteMapping("/{bookId}")
    public ResponseEntity<Void> deleteBook(
            @PathVariable Long bookId,
            @RequestParam Long deleterId) {
        User deleter = userService.getUserById(deleterId);
        bookService.deleteBook(bookId, deleter);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/author/{authorId}")
    public ResponseEntity<List<Book>> getBooksByAuthor(@PathVariable Long authorId) {
        User author = userService.getUserById(authorId);
        List<Book> books = bookService.getBooksByAuthor(author);
        return ResponseEntity.ok(books);
    }
    
    @GetMapping("/public")
    public ResponseEntity<Page<Book>> getPublicBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Book> books = bookService.getPublicBooksPaginated(pageable);
        return ResponseEntity.ok(books);
    }
    
    @GetMapping("/featured")
    public ResponseEntity<List<Book>> getFeaturedBooks() {
        List<Book> books = bookService.getFeaturedBooks();
        return ResponseEntity.ok(books);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<Book>> searchBooks(@RequestParam String title) {
        List<Book> books = bookService.searchBooksByTitle(title);
        return ResponseEntity.ok(books);
    }
    
    @GetMapping("/genre/{genre}")
    public ResponseEntity<List<Book>> getBooksByGenre(@PathVariable String genre) {
        List<Book> books = bookService.getBooksByGenre(genre);
        return ResponseEntity.ok(books);
    }
    
    @GetMapping("/recent")
    public ResponseEntity<List<Book>> getRecentBooks(
            @RequestParam(defaultValue = "5") int limit) {
        List<Book> books = bookService.getRecentPublicBooks(limit);
        return ResponseEntity.ok(books);
    }
    
    @GetMapping("/author/{authorId}/count")
    public ResponseEntity<Long> getBookCountByAuthor(@PathVariable Long authorId) {
        User author = userService.getUserById(authorId);
        Long count = bookService.getBookCountByAuthor(author);
        return ResponseEntity.ok(count);
    }
    
    @PutMapping("/{bookId}/featured")
    public ResponseEntity<Book> toggleFeatured(
            @PathVariable Long bookId,
            @RequestParam boolean featured) {
        Book book = bookService.toggleFeaturedStatus(bookId, featured);
        return ResponseEntity.ok(book);
    }
}

class BookRequest {
    private String title;
    private String description;
    private String genre;
    private PrivacyType privacy;
    private String coverImage;
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    public PrivacyType getPrivacy() { return privacy; }
    public void setPrivacy(PrivacyType privacy) { this.privacy = privacy; }
    public String getCoverImage() { return coverImage; }
    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }
}