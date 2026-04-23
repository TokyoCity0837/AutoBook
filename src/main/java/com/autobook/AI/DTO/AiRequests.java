package com.autobook.AI.DTO;

import java.util.List;

public class AiRequests {

    public record AnalyzeStyleRequest(
            String book_id,
            String author_id,
            List<String> texts
    ) {}

    public record SuggestRequest(
            String book_id,
            String current_text,
            String cursor_context
    ) {}

    public record GenerateRequest(
            String book_id,
            String context,
            Integer max_sentences,
            Double temperature
    ) {}
}