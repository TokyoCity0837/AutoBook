package com.autobook.social;

import com.autobook.book.Book;
import com.autobook.book.BookService;
import com.autobook.user.User;
import com.autobook.user.UserService;
import com.autobook.entity.EditRequestStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/edit-requests")
public class EditRequestController {
    private final EditRequestService editRequestService;
    private final BookService bookService;
    private final UserService userService;
    
    public EditRequestController(EditRequestService editRequestService, BookService bookService, UserService userService) {
        this.editRequestService = editRequestService;
        this.bookService = bookService;
        this.userService = userService;
    }
    
    @PostMapping
    public ResponseEntity<EditRequest> createEditRequest(
            @RequestParam Long bookId,
            @RequestParam Long fromUserId,
            @RequestBody EditRequestRequest request) {
        Book book = bookService.getBookById(bookId);
        User fromUser = userService.getUserById(fromUserId);
        EditRequest editRequest = editRequestService.createEditRequest(book, fromUser, request.getMessage());
        return ResponseEntity.ok(editRequest);
    }
    
    @GetMapping("/{requestId}")
    public ResponseEntity<EditRequest> getEditRequest(@PathVariable Long requestId) {
        EditRequest request = editRequestService.getEditRequestById(requestId);
        return ResponseEntity.ok(request);
    }
    
    @PutMapping("/{requestId}/approve")
    public ResponseEntity<EditRequest> approveRequest(
            @PathVariable Long requestId,
            @RequestParam Long approverUserId) {
        User approver = userService.getUserById(approverUserId);
        EditRequest request = editRequestService.approveEditRequest(requestId, approver);
        return ResponseEntity.ok(request);
    }
    
    @PutMapping("/{requestId}/reject")
    public ResponseEntity<EditRequest> rejectRequest(
            @PathVariable Long requestId,
            @RequestParam Long rejectorUserId) {
        User rejector = userService.getUserById(rejectorUserId);
        EditRequest request = editRequestService.rejectEditRequest(requestId, rejector);
        return ResponseEntity.ok(request);
    }
    
    @DeleteMapping("/{requestId}")
    public ResponseEntity<Void> cancelRequest(
            @PathVariable Long requestId,
            @RequestParam Long userId) {
        User user = userService.getUserById(userId);
        editRequestService.cancelEditRequest(requestId, user);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<EditRequest>> getRequestsForBook(@PathVariable Long bookId) {
        Book book = bookService.getBookById(bookId);
        List<EditRequest> requests = editRequestService.getEditRequestsForBook(book);
        return ResponseEntity.ok(requests);
    }
    
    @GetMapping("/book/{bookId}/pending")
    public ResponseEntity<List<EditRequest>> getPendingRequestsForBook(@PathVariable Long bookId) {
        Book book = bookService.getBookById(bookId);
        List<EditRequest> requests = editRequestService.getPendingEditRequestsForBook(book);
        return ResponseEntity.ok(requests);
    }
    
    @GetMapping("/user/{userId}/sent")
    public ResponseEntity<List<EditRequest>> getRequestsSentByUser(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        List<EditRequest> requests = editRequestService.getEditRequestsSentByUser(user);
        return ResponseEntity.ok(requests);
    }
    
    @GetMapping("/author/{authorId}/pending")
    public ResponseEntity<List<EditRequest>> getPendingRequestsForAuthor(@PathVariable Long authorId) {
        User author = userService.getUserById(authorId);
        List<EditRequest> requests = editRequestService.getPendingEditRequestsForAuthor(author);
        return ResponseEntity.ok(requests);
    }
    
    @GetMapping("/check-access")
    public ResponseEntity<Boolean> hasEditAccess(
            @RequestParam Long userId,
            @RequestParam Long bookId) {
        User user = userService.getUserById(userId);
        Book book = bookService.getBookById(bookId);
        Boolean hasAccess = editRequestService.hasEditAccessToBook(user, book);
        return ResponseEntity.ok(hasAccess);
    }
    
    @GetMapping("/stats")
    public ResponseEntity<EditRequestStats> getStats() {
        EditRequestStats stats = new EditRequestStats(
            editRequestService.getTotalEditRequestCount(),
            editRequestService.getEditRequestCountByStatus(EditRequestStatus.PENDING),
            editRequestService.getEditRequestCountByStatus(EditRequestStatus.ACCEPTED),
            editRequestService.getEditRequestCountByStatus(EditRequestStatus.REJECTED)
        );
        return ResponseEntity.ok(stats);
    }
}

class EditRequestRequest {
    private String message;
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}

record EditRequestStats(
    long total,
    long pending,
    long approved,
    long rejected
) {}