package com.autobook.social;

import org.springframework.data.jpa.repository.JpaRepository;
import com.autobook.book.Book;
import com.autobook.user.User;
import com.autobook.entity.EditRequestStatus;
import java.util.List;

public interface EditRequestRepository extends JpaRepository<EditRequest, Long> {

    List<EditRequest> findByBook(Book book);

    List<EditRequest> findByBookAndStatus(Book book, EditRequestStatus status);

    List<EditRequest> findByFromUser(User fromUser);

    List<EditRequest> findByFromUserAndStatus(User fromUser, EditRequestStatus status);

    List<EditRequest> findByBook_AuthorAndStatus(User author, EditRequestStatus status);

    Long countByStatus(EditRequestStatus status);

    Long countByBookAndStatus(Book book, EditRequestStatus status);
}