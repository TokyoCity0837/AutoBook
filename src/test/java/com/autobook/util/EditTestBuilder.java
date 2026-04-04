package com.autobook.util;

import com.autobook.Enum.EditStatus;
import com.autobook.Library.Book.Book;
import com.autobook.Library.Edit.Edit;
import com.autobook.Social.User.User;

public class EditTestBuilder {

    private Long id = 1L;
    private Book book = new BookTestBuilder().build();
    private User fromUser = new UserTestBuilder().withId(2L).build();
    private String message = "Default edit request message";
    private EditStatus status = EditStatus.PENDING;

    public EditTestBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public EditTestBuilder withBook(Book book) {
        this.book = book;
        return this;
    }

    public EditTestBuilder withFromUser(User fromUser) {
        this.fromUser = fromUser;
        return this;
    }

    public EditTestBuilder withMessage(String message) {
        this.message = message;
        return this;
    }

    public EditTestBuilder withStatus(EditStatus status) {
        this.status = status;
        return this;
    }

    public Edit build() {
        Edit edit = new Edit();
        edit.setId(id);
        edit.setBook(book);
        edit.setFromUser(fromUser);
        edit.setMessage(message);
        edit.setStatus(status);
        return edit;
    }
}