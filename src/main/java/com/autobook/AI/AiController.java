package com.autobook.AI;

import com.autobook.Exception.UserNotFoundException;
import com.autobook.AI.DTO.AiEditorRequests.ContinueTextBody;
import com.autobook.AI.DTO.AiEditorRequests.SuggestionsBody;
import com.autobook.Social.User.User;
import com.autobook.Social.User.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;
    private final UserRepository userRepository;

    private User getAuthenticatedUser(Principal principal) {
        if (principal == null) throw new UserNotFoundException("Principal is null.");
        return userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UserNotFoundException("Authenticated user not found"));
    }

    @PostMapping("/books/{bookId}/analyze-style")
    public CompletableFuture<Map<String, Object>> analyzeStyle(@PathVariable Long bookId, Principal principal) {
        return aiService.analyzeStyle(bookId, getAuthenticatedUser(principal));
    }

    @GetMapping("/books/{bookId}/style-exists")
    public Object styleExists(@PathVariable Long bookId) {
        return aiService.styleProfileExists(bookId);
    }

    @PostMapping("/books/{bookId}/suggestions")
    public CompletableFuture<Map<String, Object>> suggestions(
            @PathVariable Long bookId,
            @RequestBody(required = false) SuggestionsBody body
    ) {
        String currentText = body != null ? body.currentText() : "";
        String cursorContext = body != null ? body.cursorContext() : "";
        return aiService.getSuggestions(bookId, currentText, cursorContext);
    }

    @PostMapping("/books/{bookId}/continue")
    public CompletableFuture<Map<String, Object>> continueText(
            @PathVariable Long bookId,
            @RequestBody(required = false) ContinueTextBody body,
            Principal principal
    ) {
        String context = body != null ? body.context() : "";
        Integer maxSentences = body != null ? body.maxSentences() : 3;
        Double temperature = body != null ? body.temperature() : 0.8;

        return aiService.generateContinuation(
                bookId,
                getAuthenticatedUser(principal),
                context,
                maxSentences,
                temperature
        );
    }
}