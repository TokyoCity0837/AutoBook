package com.autobook.AI.DTO;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AiRequestsTest {
    @Test
    void testRecords() {
        AiRequests.AnalyzeStyleRequest req1 = new AiRequests.AnalyzeStyleRequest("B1", "A1", List.of("hello"));
        assertEquals("B1", req1.book_id());
        assertEquals("A1", req1.author_id());
        assertEquals(1, req1.texts().size());

        AiRequests.SuggestRequest req2 = new AiRequests.SuggestRequest("B1", "txt", "ctx");
        assertEquals("B1", req2.book_id());
        assertEquals("txt", req2.current_text());
        assertEquals("ctx", req2.cursor_context());

        AiRequests.GenerateRequest req3 = new AiRequests.GenerateRequest("B1", "ctx", 5, 0.7);
        assertEquals("B1", req3.book_id());
        assertEquals("ctx", req3.context());
        assertEquals(5, req3.max_sentences());
        assertEquals(0.7, req3.temperature());
    }
}
