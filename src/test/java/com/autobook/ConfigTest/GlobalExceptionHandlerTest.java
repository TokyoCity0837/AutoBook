package com.autobook.ConfigTest;

import com.autobook.Exception.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link GlobalExceptionHandler}.
 * Verifies that each exception type produces the correct HTTP status.
 */
@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler handler;

    // 404

    @Test
    void handleUserNotFound_returns404() {
        ResponseEntity<Object> res = handler.handleUserNotFoundException(new UserNotFoundException("not found"));
        assertEquals(404, res.getStatusCode().value());
        assertBodyHasMessage(res, "not found");
    }

    @Test
    void handleNotFoundGroup_returns404_forPost() {
        ResponseEntity<Object> res = handler.handleNotFoundExceptions(new PostNotFoundException(1L));
        assertEquals(404, res.getStatusCode().value());
    }

    @Test
    void handleNotFoundGroup_returns404_forFollow() {
        ResponseEntity<Object> res = handler.handleNotFoundExceptions(new FollowNotFoundException(2L));
        assertEquals(404, res.getStatusCode().value());
    }

    @Test
    void handleNotFoundGroup_returns404_forComment() {
        ResponseEntity<Object> res = handler.handleNotFoundExceptions(new CommentNotFoundException(3L));
        assertEquals(404, res.getStatusCode().value());
    }

    @Test
    void handleNotFoundGroup_returns404_forBook() {
        ResponseEntity<Object> res = handler.handleNotFoundExceptions(new BookNotFoundException(4L));
        assertEquals(404, res.getStatusCode().value());
    }

    @Test
    void handleNotFoundGroup_returns404_forChapter() {
        ResponseEntity<Object> res = handler.handleNotFoundExceptions(new ChapterNotFoundException(5L));
        assertEquals(404, res.getStatusCode().value());
    }

    @Test
    void handleNotFoundGroup_returns404_forEditRequest() {
        ResponseEntity<Object> res = handler.handleNotFoundExceptions(new EditRequestNotFoundException(6L));
        assertEquals(404, res.getStatusCode().value());
    }

    // 409 Conflict

    @Test
    void handleEmailAlreadyInUse_returns409() {
        ResponseEntity<Object> res = handler
                .handleEmailAlreadyInUseException(new EmailAlreadyInUseException("a@b.com"));
        assertEquals(409, res.getStatusCode().value());
    }

    @Test
    void handleUsernameAlreadyExists_returns409() {
        ResponseEntity<Object> res = handler
                .handleUsernameAlreadyExistsException(new UsernameAlreadyExistsException("user1"));
        assertEquals(409, res.getStatusCode().value());
    }

    @Test
    void handleConflictGroup_returns409_forFollowAlreadyExists() {
        ResponseEntity<Object> res = handler.handleConflictExceptions(new FollowAlreadyExistsException());
        assertEquals(409, res.getStatusCode().value());
    }

    @Test
    void handleConflictGroup_returns409_forEditRequestAlreadyExists() {
        ResponseEntity<Object> res = handler.handleConflictExceptions(new EditRequestAlreadyExistsException());
        assertEquals(409, res.getStatusCode().value());
    }

    // 400 Bad Request

    @Test
    void handleBadRequests_returns400_forInvalidFollow() {
        ResponseEntity<Object> res = handler.handleBadRequestExceptions(new InvalidFollowException("bad"));
        assertEquals(400, res.getStatusCode().value());
    }

    @Test
    void handleBadRequests_returns400_forEmptyPost() {
        ResponseEntity<Object> res = handler.handleBadRequestExceptions(new EmptyPostContentException());
        assertEquals(400, res.getStatusCode().value());
    }

    @Test
    void handleBadRequests_returns400_forEmptyComment() {
        ResponseEntity<Object> res = handler.handleBadRequestExceptions(new EmptyCommentContentException());
        assertEquals(400, res.getStatusCode().value());
    }

    @Test
    void handleBadRequests_returns400_forEmptyBook() {
        ResponseEntity<Object> res = handler.handleBadRequestExceptions(new EmptyBookTitleException());
        assertEquals(400, res.getStatusCode().value());
    }

    @Test
    void handleBadRequests_returns400_forEmptyChapter() {
        ResponseEntity<Object> res = handler.handleBadRequestExceptions(new EmptyChapterTitleException());
        assertEquals(400, res.getStatusCode().value());
    }

    @Test
    void handleBadRequests_returns400_forInvalidEditRequest() {
        ResponseEntity<Object> res = handler.handleBadRequestExceptions(new InvalidEditRequestException("bad"));
        assertEquals(400, res.getStatusCode().value());
    }

    // 500 Internal Server Error

    @Test
    void handleGlobalException_returns500() {
        ResponseEntity<Object> res = handler.handleGlobalException(new RuntimeException("boom"));
        assertEquals(500, res.getStatusCode().value());
    }

    // IllegalArgument

    @Test
    void handleIllegalArg_returnsMessage() {
        Map<String, String> res = handler.handleIllegalArg(new IllegalArgumentException("bad arg"));
        assertEquals("bad arg", res.get("message"));
    }

    // helper

    @SuppressWarnings("unchecked")
    private void assertBodyHasMessage(ResponseEntity<Object> res, String expected) {
        Map<String, Object> body = (Map<String, Object>) res.getBody();
        assertNotNull(body);
        assertTrue(body.get("message").toString().contains(expected));
    }
}
