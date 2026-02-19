package com.autobook.social;

import com.autobook.book.Book;
import com.autobook.book.BookService;
import com.autobook.user.User;
import com.autobook.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CommentController {
    private final CommentService commentService;
    private final BookService bookService;
    private final UserService userService;
    
    public CommentController(CommentService commentService, BookService bookService, UserService userService) {
        this.commentService = commentService;
        this.bookService = bookService;
        this.userService = userService;
    }
    
    @PostMapping("/books/{bookId}/comments")
    public ResponseEntity<Comment> addComment(
            @PathVariable Long bookId,
            @RequestParam Long userId,
            @RequestBody CommentRequest request) {
        Book book = bookService.getBookById(bookId);
        User user = userService.getUserById(userId);
        Comment comment = commentService.createComment(book, user, request.getContent());
        return ResponseEntity.ok(comment);
    }
    
    @GetMapping("/books/{bookId}/comments")
    public ResponseEntity<List<Comment>> getBookComments(@PathVariable Long bookId) {
        Book book = bookService.getBookById(bookId);
        List<Comment> comments = commentService.findAllCommentsByBook(book);
        return ResponseEntity.ok(comments);
    }
    
    @GetMapping("/users/{userId}/comments")
    public ResponseEntity<List<Comment>> getUserComments(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        List<Comment> comments = commentService.findAllCommentsByUser(user);
        return ResponseEntity.ok(comments);
    }
    
    @GetMapping("/comments/{commentId}")
    public ResponseEntity<Comment> getCommentById(@PathVariable Long commentId) {
        Comment comment = commentService.getCommentById(commentId);
        return ResponseEntity.ok(comment);
    }
    
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<Comment> updateComment(
            @PathVariable Long commentId,
            @RequestBody CommentRequest request) {
        Comment comment = commentService.updateComment(commentId, request.getContent());
        return ResponseEntity.ok(comment);
    }
    
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/books/{bookId}/comments/count")
    public ResponseEntity<Long> getCommentCount(@PathVariable Long bookId) {
        Book book = bookService.getBookById(bookId);
        Long count = commentService.getCommentCountByBook(book);
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/books/{bookId}/comments/search")
    public ResponseEntity<List<Comment>> searchComments(
            @PathVariable Long bookId,
            @RequestParam String query) {
        Book book = bookService.getBookById(bookId);
        List<Comment> comments = commentService.searchCommentsInBook(book, query);
        return ResponseEntity.ok(comments);
    }
    
    @GetMapping("/comments/search")
    public ResponseEntity<List<Comment>> searchAllComments(@RequestParam String query) {
        List<Comment> comments = commentService.searchComments(query);
        return ResponseEntity.ok(comments);
    }
}

class CommentRequest {
    private String content;
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}