package com.autobook;

import com.autobook.Exception.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ExceptionTest {

    @Test
    void testExceptions() {
        assertNotNull(new BookNotFoundException(1L));
        assertNotNull(new ChapterNotFoundException(1L));
        assertNotNull(new CommentNotFoundException(1L));
        assertNotNull(new EditRequestAlreadyExistsException());
        assertNotNull(new EditRequestNotFoundException(1L));
        assertNotNull(new EmailAlreadyInUseException("test@test.com"));
        assertNotNull(new EmptyBookTitleException());
        assertNotNull(new EmptyChapterTitleException());
        assertNotNull(new EmptyCommentContentException());
        assertNotNull(new EmptyPostContentException());
        assertNotNull(new FollowAlreadyExistsException());
        assertNotNull(new FollowNotFoundException(1L));
        assertNotNull(new InvalidEditRequestException("msg"));
        assertNotNull(new InvalidFollowException("msg"));
        assertNotNull(new PostNotFoundException(1L));
        assertNotNull(new UserNotFoundException("msg"));
        assertNotNull(new UsernameAlreadyExistsException("username"));
    }
}
