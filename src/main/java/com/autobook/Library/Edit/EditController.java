package com.autobook.Library.Edit;

import com.autobook.Library.Book.Book;
import com.autobook.Library.Book.BookRepository;
import com.autobook.Exception.BookNotFoundException;
import com.autobook.Exception.UserNotFoundException;
import com.autobook.Library.Edit.DTO.Request.CreateEditRequest;
import com.autobook.Library.Edit.DTO.Response.EditResponse;
import com.autobook.Social.User.User;
import com.autobook.Social.User.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/edits")
@RequiredArgsConstructor
public class EditController {

    private final EditService editService;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    private User getAuthenticatedUser(Principal principal) {
        if (principal == null) throw new UserNotFoundException("Principal is null");
        return userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    @PostMapping("/book/{bookId}")
    @ResponseStatus(HttpStatus.CREATED)
    public EditResponse createEditRequest(@PathVariable Long bookId, @RequestBody CreateEditRequest request, Principal principal) {
        User fromUser = getAuthenticatedUser(principal);
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));
        return editService.createEditRequest(book, fromUser, request);
    }

    @GetMapping("/{id}")
    public EditResponse getEditRequest(@PathVariable Long id) {
        return editService.getEditRequestById(id);
    }

    @GetMapping("/book/{bookId}")
    public List<EditResponse> getEditRequestsByBook(@PathVariable Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));
        return editService.getEditRequestsByBook(book);
    }

    @GetMapping("/book/{bookId}/pending")
    public List<EditResponse> getPendingEditRequestsByBook(@PathVariable Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));
        return editService.getPendingEditRequestsByBook(book);
    }

    @GetMapping("/user/me")
    public List<EditResponse> getMyEditRequests(Principal principal) {
        User user = getAuthenticatedUser(principal);
        return editService.getEditRequestsByUser(user);
    }

    @GetMapping("/user/me/pending")
    public List<EditResponse> getMyPendingEditRequests(Principal principal) {
        User user = getAuthenticatedUser(principal);
        return editService.getPendingEditRequestsByUser(user);
    }

    @GetMapping("/received")
    public List<EditResponse> getReceivedEditRequests(Principal principal) {
        User author = getAuthenticatedUser(principal);
        return editService.getReceivedEditRequests(author);
    }

    @GetMapping("/received/pending")
    public List<EditResponse> getReceivedPendingEditRequests(Principal principal) {
        User author = getAuthenticatedUser(principal);
        return editService.getReceivedPendingEditRequests(author);
    }

    @PutMapping("/{id}/accept")
    public EditResponse acceptEditRequest(@PathVariable Long id) {
        return editService.acceptEditRequest(id);
    }

    @PutMapping("/{id}/reject")
    public EditResponse rejectEditRequest(@PathVariable Long id) {
        return editService.rejectEditRequest(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEditRequest(@PathVariable Long id) {
        editService.deleteEditRequest(id);
    }
}
