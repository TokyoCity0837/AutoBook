package com.autobook.Factory;

import com.autobook.Enum.EditStatus;
import com.autobook.Library.Book.Book;
import com.autobook.Library.Edit.Edit;
import com.autobook.Social.User.User;
import org.springframework.stereotype.Component;

@Component
public class EditFactory {

    public Edit create(Book book, User fromUser, String message, EditStatus status) {
        Edit edit = new Edit();
        edit.setBook(book);
        edit.setFromUser(fromUser);
        edit.setMessage(message);
        edit.setStatus(status != null ? status : EditStatus.PENDING);
        return edit;
    }
}