package com.autobook.Library.SavedItem;

import com.autobook.Library.SavedItem.DTO.Response.SavedItemResponse;
import com.autobook.Social.User.User;
import com.autobook.Social.User.UserRepository;
import com.autobook.Exception.UserNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/library")
public class SavedItemController {

    private final SavedItemService savedItemService;
    private final UserRepository userRepository;

    public SavedItemController(SavedItemService savedItemService, UserRepository userRepository) {
        this.savedItemService = savedItemService;
        this.userRepository = userRepository;
    }

    private User getAuthenticatedUser(Principal principal) {
        if (principal == null) throw new UserNotFoundException("Principal is null");
        return userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    @PostMapping("/book/{bookId}")
    public ResponseEntity<Void> toggleSaveBook(Principal principal, @PathVariable Long bookId) {
        savedItemService.toggleSaveBook(getAuthenticatedUser(principal), bookId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/book/{bookId}/status")
    public ResponseEntity<Boolean> getBookSaveStatus(Principal principal, @PathVariable Long bookId) {
        return ResponseEntity.ok(savedItemService.isBookSaved(getAuthenticatedUser(principal), bookId));
    }

    @PostMapping("/chapter/{chapterId}")
    public ResponseEntity<Void> toggleSaveChapter(Principal principal, @PathVariable Long chapterId) {
        savedItemService.toggleSaveChapter(getAuthenticatedUser(principal), chapterId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/my")
    public ResponseEntity<List<SavedItemResponse>> getMySavedItems(Principal principal) {
        return ResponseEntity.ok(savedItemService.getMySavedItems(getAuthenticatedUser(principal)));
    }
}
