package com.autobook.Library.Edit;

import com.autobook.Enum.EditStatus;
import com.autobook.Exception.EditRequestAlreadyExistsException;
import com.autobook.Exception.EditRequestNotFoundException;
import com.autobook.Exception.InvalidEditRequestException;
import com.autobook.Factory.EditFactory;
import com.autobook.Library.Book.Book;
import com.autobook.Library.Edit.DTO.Request.CreateEditRequest;
import com.autobook.Library.Edit.DTO.Response.EditResponse;
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
    private final EditMapper editMapper;

    @Transactional
    public EditResponse createEditRequest(Book book, User fromUser, CreateEditRequest request) {
        validateEditRequest(book, fromUser);

        boolean existsPendingRequest = editRepository.findByBookAndStatus(book, EditStatus.PENDING)
                .stream()
                .anyMatch(edit -> edit.getFromUser().getId().equals(fromUser.getId()));

        if (existsPendingRequest) {
            throw new EditRequestAlreadyExistsException();
        }

        Edit edit = editFactory.create(book, fromUser, request.message(), EditStatus.PENDING);
        Edit savedEdit = editRepository.save(edit);

        return editMapper.toResponse(savedEdit);
    }

    public EditResponse getEditRequestById(Long editRequestId) {
        Edit edit = findEditById(editRequestId);
        return editMapper.toResponse(edit);
    }

    public List<EditResponse> getEditRequestsByBook(Book book) {
        return editRepository.findByBook(book)
                .stream()
                .map(editMapper::toResponse)
                .toList();
    }

    public List<EditResponse> getPendingEditRequestsByBook(Book book) {
        return editRepository.findByBookAndStatus(book, EditStatus.PENDING)
                .stream()
                .map(editMapper::toResponse)
                .toList();
    }

    public List<EditResponse> getEditRequestsByUser(User user) {
        return editRepository.findByFromUser(user)
                .stream()
                .map(editMapper::toResponse)
                .toList();
    }

    public List<EditResponse> getPendingEditRequestsByUser(User user) {
        return editRepository.findByFromUserAndStatus(user, EditStatus.PENDING)
                .stream()
                .map(editMapper::toResponse)
                .toList();
    }

    public List<EditResponse> getReceivedEditRequests(User author) {
        return editRepository.findByBook_Author(author)
                .stream()
                .map(editMapper::toResponse)
                .toList();
    }

    public List<EditResponse> getReceivedPendingEditRequests(User author) {
        return editRepository.findByBook_AuthorAndStatus(author, EditStatus.PENDING)
                .stream()
                .map(editMapper::toResponse)
                .toList();
    }

    public Long countPendingRequests() {
        return editRepository.countByStatus(EditStatus.PENDING);
    }

    public Long countPendingRequestsByBook(Book book) {
        return editRepository.countByBookAndStatus(book, EditStatus.PENDING);
    }

    @Transactional
    public EditResponse acceptEditRequest(Long editRequestId) {
        Edit edit = findEditById(editRequestId);

        if (edit.getStatus() != EditStatus.PENDING) {
            throw new InvalidEditRequestException();
        }

        edit.setStatus(EditStatus.ACCEPTED);
        Edit savedEdit = editRepository.save(edit);

        return editMapper.toResponse(savedEdit);
    }

    @Transactional
    public EditResponse rejectEditRequest(Long editRequestId) {
        Edit edit = findEditById(editRequestId);

        if (edit.getStatus() != EditStatus.PENDING) {
            throw new InvalidEditRequestException();
        }

        edit.setStatus(EditStatus.REJECTED);
        Edit savedEdit = editRepository.save(edit);

        return editMapper.toResponse(savedEdit);
    }

    @Transactional
    public void deleteEditRequest(Long editRequestId) {
        Edit edit = findEditById(editRequestId);
        editRepository.delete(edit);
    }

    private Edit findEditById(Long editRequestId) {
        return editRepository.findById(editRequestId)
                .orElseThrow(() -> new EditRequestNotFoundException(editRequestId));
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