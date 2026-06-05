package com.autobook.ServiceTest;

import com.autobook.AI.AiService;
import com.autobook.AI.config.AiApiProperties;
import com.autobook.Library.Chapter.Chapter;
import com.autobook.Library.Chapter.ChapterRepository;
import com.autobook.Social.User.User;
import com.autobook.util.UserTestBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AiServiceTest {

    @Mock
    private RestTemplate aiRestTemplate;

    @Mock
    private AiApiProperties props;

    @Mock
    private ChapterRepository chapterRepository;

    @InjectMocks
    private AiService aiService;

    @Test
    void analyzeStyle_EmptyChapters() {
        User user = new UserTestBuilder().withId(1L).build();
        when(chapterRepository.findByBookIdOrderByCreatedAtAsc(1L)).thenReturn(Collections.emptyList());

        Map<String, Object> result = aiService.analyzeStyle(1L, user).join();

        assertEquals("No chapter text to analyze", result.get("message"));
    }

    @Test
    void getSuggestions_CyrillicCheck() {
        assertThrows(IllegalArgumentException.class,
                () -> aiService.getSuggestions(1L, "Some text", "і ще трохи кирилиці").join());
    }

    @Test
    void generateContinuation_CyrillicCheck() {
        User user = new UserTestBuilder().withId(1L).build();
        assertThrows(IllegalArgumentException.class,
                () -> aiService.generateContinuation(1L, user, "Початок", 3, 0.8).join());
    }

    @Test
    void analyzeStyle_ThrowsExceptionOnBadUrl() {
        User user = new UserTestBuilder().withId(1L).build();
        Chapter ch = new Chapter();
        ch.setContent("Hello English");
        when(chapterRepository.findByBookIdOrderByCreatedAtAsc(1L)).thenReturn(List.of(ch));
        when(props.getBaseUrl()).thenReturn("httpxx://invalid");

        assertThrows(RuntimeException.class, () -> aiService.analyzeStyle(1L, user).join());
    }

    @Test
    void getSuggestions_ThrowsExceptionOnBadUrl() {
        when(props.getBaseUrl()).thenReturn("httpxx://invalid");
        assertThrows(RuntimeException.class, () -> aiService.getSuggestions(1L, "text", "ctx").join());
    }

    @Test
    void generateContinuation_ThrowsExceptionOnBadUrl() {
        User user = new UserTestBuilder().withId(1L).build();
        when(props.getBaseUrl()).thenReturn("httpxx://invalid");
        assertThrows(RuntimeException.class, () -> aiService.generateContinuation(1L, user, "ctx", 5, 0.5).join());
    }

    @Test
    void generateContinuation_NullParams() {
        User user = new UserTestBuilder().withId(1L).build();
        when(props.getBaseUrl()).thenReturn("httpxx://invalid");
        assertThrows(RuntimeException.class, () -> aiService.generateContinuation(1L, user, "ctx", null, null).join());
    }

    @Test
    void styleProfileExists_Ok() {
        when(props.getBaseUrl()).thenReturn("http://ai");
        when(aiRestTemplate.getForObject("http://ai/api/v1/analyze/1/exists", Object.class)).thenReturn("yes");
        assertEquals("yes", aiService.styleProfileExists(1L));
    }

    @Test
    void getSuggestions_throwsIfCyrillic() {
        assertThrows(IllegalArgumentException.class,
                () -> aiService.getSuggestions(1L, "Привіт світ", "context"));
    }

    @Test
    void generateContinuation_throwsIfCyrillic() {
        User user = new UserTestBuilder().withId(1L).build();
        assertThrows(IllegalArgumentException.class,
                () -> aiService.generateContinuation(1L, user, "Контекст", 3, 0.5));
    }

    @Test
    void styleProfileExists() {
        when(props.getBaseUrl()).thenReturn("http://localhost:5000");
        when(aiRestTemplate.getForObject("http://localhost:5000/api/v1/analyze/10/exists", Object.class)).thenReturn(true);

        Object result = aiService.styleProfileExists(10L);
        assert(result != null);
    }

}
