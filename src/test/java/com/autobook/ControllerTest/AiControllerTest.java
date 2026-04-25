package com.autobook.ControllerTest;

import com.autobook.AI.AiController;
import com.autobook.AI.AiService;
import com.autobook.AI.DTO.AiEditorRequests.ContinueTextBody;
import com.autobook.AI.DTO.AiEditorRequests.SuggestionsBody;
import com.autobook.Exception.UserNotFoundException;
import com.autobook.Social.User.User;
import com.autobook.Social.User.UserRepository;
import com.autobook.util.UserTestBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Principal;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AiControllerTest {

    @Mock
    private AiService aiService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AiController aiController;

    private Principal createPrincipal(String name) {
        return () -> name;
    }

    @Test
    void analyzeStyle_ok() {
        Principal principal = createPrincipal("user");
        User user = new UserTestBuilder().withId(1L).withUsername("user").build();

        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(aiService.analyzeStyle(10L, user))
                .thenReturn(java.util.concurrent.CompletableFuture.completedFuture(Map.of("res", "val")));

        Map<String, Object> result = aiController.analyzeStyle(10L, principal).join();
        assertEquals("val", result.get("res"));
    }

    @Test
    void analyzeStyle_noUser() {
        assertThrows(UserNotFoundException.class, () -> aiController.analyzeStyle(10L, null));
    }

    @Test
    void styleExists() {
        when(aiService.styleProfileExists(2L)).thenReturn("yes");
        Object result = aiController.styleExists(2L);
        assertEquals("yes", result);
    }

    @Test
    void suggestions_withBody() {
        SuggestionsBody body = new SuggestionsBody("cur", "ctx");
        when(aiService.getSuggestions(1L, "cur", "ctx"))
                .thenReturn(java.util.concurrent.CompletableFuture.completedFuture(Map.of("a", "b")));
        Map<String, Object> res = aiController.suggestions(1L, body).join();
        assertEquals("b", res.get("a"));
    }

    @Test
    void suggestions_nullBody() {
        when(aiService.getSuggestions(1L, "", ""))
                .thenReturn(java.util.concurrent.CompletableFuture.completedFuture(Map.of("a", "empty")));
        Map<String, Object> res = aiController.suggestions(1L, null).join();
        assertEquals("empty", res.get("a"));
    }

    @Test
    void continueText_withBody() {
        Principal principal = createPrincipal("user");
        User user = new UserTestBuilder().withId(1L).withUsername("user").build();
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));

        ContinueTextBody body = new ContinueTextBody("ctx", 5, 0.5);
        when(aiService.generateContinuation(1L, user, "ctx", 5, 0.5))
                .thenReturn(java.util.concurrent.CompletableFuture.completedFuture(Map.of("r", "done")));

        Map<String, Object> res = aiController.continueText(1L, body, principal).join();
        assertEquals("done", res.get("r"));
    }

    @Test
    void continueText_nullBody() {
        Principal principal = createPrincipal("user");
        User user = new UserTestBuilder().withId(1L).withUsername("user").build();
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));

        when(aiService.generateContinuation(1L, user, "", 3, 0.8))
                .thenReturn(java.util.concurrent.CompletableFuture.completedFuture(Map.of("r", "def")));

        Map<String, Object> res = aiController.continueText(1L, null, principal).join();
        assertEquals("def", res.get("r"));
    }
}
