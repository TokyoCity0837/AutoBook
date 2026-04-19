package com.autobook.Library.BookComment;

import com.autobook.Exception.BookNotFoundException;
import com.autobook.Exception.UserNotFoundException;
import com.autobook.Library.Book.Book;
import com.autobook.Library.Book.BookRepository;
import com.autobook.Social.User.User;
import com.autobook.Social.User.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/book-comments")
@RequiredArgsConstructor
public class BookCommentController {

    private final BookCommentService bookCommentService;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    private User getAuthenticatedUser(Principal principal) {
        if (principal == null) throw new UserNotFoundException("Principal is null");
        return userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    @PostMapping("/book/{bookId}")
    @ResponseStatus(HttpStatus.CREATED)
    public BookCommentResponse createComment(
            @PathVariable Long bookId,
            @Valid @RequestBody CreateBookCommentRequest request,
            Principal principal) {

        User author = getAuthenticatedUser(principal);
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));

        return bookCommentService.createComment(request, author, book);
    }

    @GetMapping("/book/{bookId}")
    public List<BookCommentResponse> getCommentsByBook(@PathVariable Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));
        return bookCommentService.getCommentsByBook(book);
    }

    @PutMapping("/{id}/like")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void likeComment(@PathVariable Long id) {
        bookCommentService.incrementLikeCount(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long id, Principal principal) {
        User author = getAuthenticatedUser(principal);
        bookCommentService.deleteCommentByIdAndAuthor(id, author);
    }
}
