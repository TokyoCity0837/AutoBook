package com.autobook.Library.Edit;

import com.autobook.Enum.EditStatus;
import com.autobook.Exception.EditRequestAlreadyExistsException;
import com.autobook.Exception.EditRequestNotFoundException;
import com.autobook.Exception.InvalidEditRequestException;
import com.autobook.Factory.EditFactory;
import com.autobook.Library.Book.Book;
import com.autobook.Social.User.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EditService {

    private final EditRepository editRepository;
    private final EditFactory editFactory;

    @Transactional
    public Edit createEditRequest(Book book, User fromUser, String message) {
        validateEditRequest(book, fromUser);

        boolean existsPendingRequest = editRepository.findByBookAndStatus(book, EditStatus.PENDING)
                .stream()
                .anyMatch(edit -> edit.getFromUser().getId().equals(fromUser.getId()));

        if (existsPendingRequest) {
            throw new EditRequestAlreadyExistsException();
        }

        Edit edit = editFactory.create(book, fromUser, message, EditStatus.PENDING);
        return editRepository.save(edit);
    }

    public Edit getEditRequestById(Long editRequestId) {
        return editRepository.findById(editRequestId)
                .orElseThrow(() -> new EditRequestNotFoundException(editRequestId));
    }

    public List<Edit> getEditRequestsByBook(Book book) {
        return editRepository.findByBook(book);
    }

    public List<Edit> getPendingEditRequestsByBook(Book book) {
        return editRepository.findByBookAndStatus(book, EditStatus.PENDING);
    }

    public List<Edit> getEditRequestsByUser(User user) {
        return editRepository.findByFromUser(user);
    }

    public List<Edit> getPendingEditRequestsByUser(User user) {
        return editRepository.findByFromUserAndStatus(user, EditStatus.PENDING);
    }

    public List<Edit> getReceivedEditRequests(User author) {
        return editRepository.findByBook_Author(author);
    }

    public List<Edit> getReceivedPendingEditRequests(User author) {
        return editRepository.findByBook_AuthorAndStatus(author, EditStatus.PENDING);
    }

    public Long countPendingRequests() {
        return editRepository.countByStatus(EditStatus.PENDING);
    }

    public Long countPendingRequestsByBook(Book book) {
        return editRepository.countByBookAndStatus(book, EditStatus.PENDING);
    }

    @Transactional
    public Edit acceptEditRequest(Long editRequestId) {
        Edit edit = getEditRequestById(editRequestId);

        if (edit.getStatus() != EditStatus.PENDING) {
            throw new InvalidEditRequestException();
        }

        edit.setStatus(EditStatus.ACCEPTED);
        return editRepository.save(edit);
    }

    @Transactional
    public Edit rejectEditRequest(Long editRequestId) {
        Edit edit = getEditRequestById(editRequestId);

        if (edit.getStatus() != EditStatus.PENDING) {
            throw new InvalidEditRequestException();
        }

        edit.setStatus(EditStatus.REJECTED);
        return editRepository.save(edit);
    }

    @Transactional
    public void deleteEditRequest(Long editRequestId) {
        Edit edit = getEditRequestById(editRequestId);
        editRepository.delete(edit);
    }

    private void validateEditRequest(Book book, User fromUser) {
        if (book == null || fromUser == null) {
            throw new InvalidEditRequestException();
        }

        if (book.getAuthor() != null && book.getAuthor().getId().equals(fromUser.getId())) {
            throw new InvalidEditRequestException();
        }
    }
}