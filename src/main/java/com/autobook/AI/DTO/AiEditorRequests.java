package com.autobook.AI.DTO;

public class AiEditorRequests {

    public record ContinueTextBody(
            String context,
            Integer maxSentences,
            Double temperature
    ) {}

    public record SuggestionsBody(
            String currentText,
            String cursorContext
    ) {}
}