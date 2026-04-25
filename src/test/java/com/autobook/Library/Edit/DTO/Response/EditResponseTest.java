package com.autobook.Library.Edit.DTO.Response;

import com.autobook.Enum.EditStatus;
import com.autobook.Enum.UserRole;
import com.autobook.Social.User.DTO.Response.UserCardResponse;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EditResponseTest {
    @Test
    void testRecord() {
        UserCardResponse user = new UserCardResponse(1L, "Bob", "bob", "img.png", UserRole.USER, false);
        LocalDateTime now = LocalDateTime.now();
        EditResponse r = new EditResponse(10L, user, "msg", EditStatus.PENDING, now);

        assertEquals(10L, r.id());
        assertEquals(user, r.fromUser());
        assertEquals("msg", r.message());
        assertEquals(EditStatus.PENDING, r.status());
        assertEquals(now, r.createdAt());
    }
}
