package com.autobook.Library.Edit;

import org.springframework.data.jpa.repository.JpaRepository;
import com.autobook.Library.Book.Book;
import com.autobook.Social.User.User;
import com.autobook.Enum.EditStatus;
import java.util.List;

public interface EditRepository extends JpaRepository<Edit, Long> {

    List<Edit> findByBook(Book book);

    List<Edit> findByBookAndStatus(Book book, EditStatus status);

    List<Edit> findByFromUser(User fromUser);

    List<Edit> findByFromUserAndStatus(User fromUser, EditStatus status);

    List<Edit> findByBook_Author(User author);

    List<Edit> findByBook_AuthorAndStatus(User author, EditStatus status);

    Long countByStatus(EditStatus status);

    Long countByBookAndStatus(Book book, EditStatus status);
}