package com.autobook.AI.DTO;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AiEditorRequestsTest {
    @Test
    void testRecords() {
        AiEditorRequests.ContinueTextBody req1 = new AiEditorRequests.ContinueTextBody("ctx", 3, 0.4);
        assertEquals("ctx", req1.context());
        assertEquals(3, req1.maxSentences());
        assertEquals(0.4, req1.temperature());

        AiEditorRequests.SuggestionsBody req2 = new AiEditorRequests.SuggestionsBody("cur", "ctx");
        assertEquals("cur", req2.currentText());
        assertEquals("ctx", req2.cursorContext());
    }
}
