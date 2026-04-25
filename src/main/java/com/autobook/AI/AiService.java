package com.autobook.AI;

import com.autobook.AI.config.AiApiProperties;
import com.autobook.Library.Chapter.Chapter;
import com.autobook.Library.Chapter.ChapterRepository;
import com.autobook.Social.User.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.concurrent.CompletableFuture;
import org.springframework.scheduling.annotation.Async;

/**
 * Service for communicating with the external AutoBook AI Python microservice.
 * <p>
 * Sends book chapter texts for style analysis, retrieves writing suggestions
 * and generates text continuations. All AI feature inputs must be in English;
 * Cyrillic text is rejected with an {@link IllegalArgumentException}.
 * HTTP communication is performed via raw {@link java.net.HttpURLConnection}
 * to avoid blocking the Spring thread pool.
 */
@Service
@RequiredArgsConstructor
public class AiService {

    private final RestTemplate aiRestTemplate;
    private final AiApiProperties props;
    private final ChapterRepository chapterRepository;

    private static final Pattern CYRILLIC = Pattern.compile(".*[\\p{IsCyrillic}].*");

    @Async
    public CompletableFuture<Map<String, Object>> analyzeStyle(Long bookId, User user) {
        List<Chapter> chapters = chapterRepository.findByBookIdOrderByCreatedAtAsc(bookId);

        List<String> texts = chapters.stream()
                .map(ch -> ch.getContent() == null ? "" : stripHtml(ch.getContent()))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .toList();

        if (texts.isEmpty())
            return CompletableFuture.completedFuture(Map.of("message", "No chapter text to analyze"));

        Map<String, Object> payload = Map.of(
                "book_id", String.valueOf(bookId),
                "author_id", String.valueOf(user.getId()),
                "texts", texts);

        return CompletableFuture.completedFuture(post("/api/v1/analyze", payload));
    }

    @Async
    public CompletableFuture<Map<String, Object>> getSuggestions(Long bookId, String currentText,
            String cursorContext) {
        String text = safe(currentText);
        String ctx = safe(cursorContext);
        enforceEnglish(text + " " + ctx);

        Map<String, Object> payload = Map.of(
                "book_id", String.valueOf(bookId),
                "current_text", text,
                "cursor_context", ctx);

        return CompletableFuture.completedFuture(post("/api/v1/suggest", payload));
    }

    @Async
    public CompletableFuture<Map<String, Object>> generateContinuation(Long bookId, User user, String context,
            Integer maxSentences, Double temperature) {
        String ctx = safe(context);
        enforceEnglish(ctx);

        Map<String, Object> payload = Map.of(
                "author_id", String.valueOf(bookId),
                "context", ctx,
                "max_sentences", maxSentences == null ? 3 : maxSentences,
                "temperature", temperature == null ? 0.8 : temperature);

        return CompletableFuture.completedFuture(post("/api/v1/generate", payload));
    }

    public Object styleProfileExists(Long bookId) {
        String url = props.getBaseUrl() + "/api/v1/analyze/" + bookId + "/exists";
        return aiRestTemplate.getForObject(url, Object.class);
    }

    private Map<String, Object> post(String path, Map<String, Object> payload) {
        try {
            String base = props.getBaseUrl();
            if (base.endsWith("/"))
                base = base.substring(0, base.length() - 1);

            String p = path.startsWith("/") ? path : "/" + path;

            String urlString = base + p;

            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            String json = mapper.writeValueAsString(payload);

            java.net.URL url = new java.net.URL(urlString);
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(props.getTimeoutMs());
            conn.setReadTimeout(props.getTimeoutMs());
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setRequestProperty("Accept", "application/json");

            try (java.io.OutputStream os = conn.getOutputStream()) {
                byte[] input = json.getBytes(java.nio.charset.StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int status = conn.getResponseCode();
            java.io.InputStream is = (status >= 200 && status < 300)
                    ? conn.getInputStream()
                    : conn.getErrorStream();

            String responseBody;
            try (java.io.BufferedReader br = new java.io.BufferedReader(
                    new java.io.InputStreamReader(is, java.nio.charset.StandardCharsets.UTF_8))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null)
                    sb.append(line);
                responseBody = sb.toString();
            }

            if (status < 200 || status >= 300) {
                throw new RuntimeException("AI API error " + status + ": " + responseBody);
            }

            return mapper.readValue(responseBody, Map.class);

        } catch (Exception e) {
            throw new RuntimeException("AI POST failed: " + e.getMessage(), e);
        }
    }

    private void enforceEnglish(String text) {
        if (text == null || text.isBlank())
            return;
        if (CYRILLIC.matcher(text).matches()) {
            throw new IllegalArgumentException("Only English text is supported for AI features.");
        }
    }

    private String safe(String v) {
        return v == null ? "" : v;
    }

    private String stripHtml(String html) {
        return html.replaceAll("<[^>]*>", " ").replaceAll("\\s+", " ").trim();
    }
}