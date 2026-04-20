package com.autobook.Library.Book;

import com.autobook.Enum.PrivacyType;
import com.autobook.Library.Book.DTO.Request.CreateBookRequest;
import com.autobook.Library.Book.DTO.Request.UpdateBookRequest;
import com.autobook.Library.Book.DTO.Response.BookCardResponse;
import com.autobook.Library.Book.DTO.Response.BookDetailsResponse;
import com.autobook.Social.User.User;
import com.autobook.Social.User.UserRepository;
import com.autobook.Exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final UserRepository userRepository;

    private User getAuthenticatedUser(Principal principal) {
        if (principal == null) throw new UserNotFoundException("Principal is null");
        return userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDetailsResponse createBook(@RequestBody CreateBookRequest request, Principal principal) {
        User author = getAuthenticatedUser(principal);
        return bookService.createBook(author, request);
    }

    @GetMapping("/{id}")
    public BookDetailsResponse getBook(@PathVariable Long id) {
        return bookService.getBookById(id);
    }

    @GetMapping
    public List<BookCardResponse> getAllBooks() {
        return bookService.getAllBooks();
    }

    @GetMapping("/author/me")
    public List<BookCardResponse> getMyBooks(Principal principal) {
        User author = getAuthenticatedUser(principal);
        return bookService.getBooksByAuthorOrdered(author);
    }

    @GetMapping("/author/{userId}")
    public List<BookCardResponse> getBooksByAuthor(@PathVariable Long userId) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return bookService.getBooksByAuthorOrdered(author);
    }

    @GetMapping("/privacy/{privacy}")
    public List<BookCardResponse> getBooksByPrivacy(@PathVariable PrivacyType privacy) {
        return bookService.getBooksByPrivacy(privacy);
    }

    @GetMapping("/author/{userId}/privacy/{privacy}")
    public List<BookCardResponse> getBooksByAuthorAndPrivacy(@PathVariable Long userId, @PathVariable PrivacyType privacy) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return bookService.getBooksByAuthorAndPrivacy(author, privacy);
    }

    @GetMapping("/search")
    public List<BookCardResponse> searchBooks(@RequestParam String title) {
        return bookService.searchBooksByTitle(title);
    }

    @GetMapping("/genre/{genre}")
    public List<BookCardResponse> getBooksByGenre(@PathVariable String genre) {
        return bookService.getBooksByGenre(genre);
    }

    @PutMapping("/{id}")
    public BookDetailsResponse updateBook(@PathVariable Long id, @RequestBody UpdateBookRequest request) {
        return bookService.updateBook(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
    }
}
