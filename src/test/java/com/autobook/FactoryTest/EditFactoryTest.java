package com.autobook.FactoryTest;

import com.autobook.Enum.EditStatus;
import com.autobook.Factory.EditFactory;
import com.autobook.Library.Book.Book;
import com.autobook.Library.Edit.Edit;
import com.autobook.Social.User.User;
import com.autobook.util.BookTestBuilder;
import com.autobook.util.UserTestBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EditFactoryTest {

    private final EditFactory editFactory = new EditFactory();

    @Test
    void create_shouldCreateEditWithProvidedValues() {
        Book book = new BookTestBuilder().build();
        User fromUser = new UserTestBuilder().withId(2L).build();

        Edit edit = editFactory.create(
                book,
                fromUser,
                "Please allow me to edit this book",
                EditStatus.PENDING
        );

        assertNotNull(edit);
        assertEquals(book, edit.getBook());
        assertEquals(fromUser, edit.getFromUser());
        assertEquals("Please allow me to edit this book", edit.getMessage());
        assertEquals(EditStatus.PENDING, edit.getStatus());
    }

    @Test
    void create_shouldSetPendingStatus_whenStatusIsNull() {
        Book book = new BookTestBuilder().build();
        User fromUser = new UserTestBuilder().withId(2L).build();

        Edit edit = editFactory.create(
                book,
                fromUser,
                "Message",
                null
        );

        assertNotNull(edit);
        assertEquals(book, edit.getBook());
        assertEquals(fromUser, edit.getFromUser());
        assertEquals("Message", edit.getMessage());
        assertEquals(EditStatus.PENDING, edit.getStatus());
    }

    @Test
    void create_shouldSetAcceptedStatus_whenAcceptedPassed() {
        Book book = new BookTestBuilder().build();
        User fromUser = new UserTestBuilder().withId(2L).build();

        Edit edit = editFactory.create(
                book,
                fromUser,
                "Accepted request",
                EditStatus.ACCEPTED
        );

        assertEquals(EditStatus.ACCEPTED, edit.getStatus());
    }

    @Test
    void create_shouldSetRejectedStatus_whenRejectedPassed() {
        Book book = new BookTestBuilder().build();
        User fromUser = new UserTestBuilder().withId(2L).build();

        Edit edit = editFactory.create(
                book,
                fromUser,
                "Rejected request",
                EditStatus.REJECTED
        );

        assertEquals(EditStatus.REJECTED, edit.getStatus());
    }
}