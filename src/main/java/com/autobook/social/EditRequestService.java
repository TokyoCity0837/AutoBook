package com.autobook.social;

import com.autobook.book.Book;
import com.autobook.user.User;
import com.autobook.entity.EditRequestStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class EditRequestService {
    private final EditRequestRepository editRequestRepository;
    
    public EditRequestService(EditRequestRepository editRequestRepository) {
        this.editRequestRepository = editRequestRepository;
    }
    
    public EditRequest createEditRequest(Book book, User fromUser, String message) {
        if (book.getAuthor().getId().equals(fromUser.getId())) {
            throw new RuntimeException("You are already the author of this book");
        }
        
        List<EditRequest> existingRequests = editRequestRepository.findByBookAndStatus(book, EditRequestStatus.PENDING);
        boolean alreadyRequested = existingRequests.stream()
                .anyMatch(request -> request.getFromUser().getId().equals(fromUser.getId()));
        
        if (alreadyRequested) {
            throw new RuntimeException("You already have a pending edit request for this book");
        }
        
        EditRequest editRequest = new EditRequest();
        editRequest.setBook(book);
        editRequest.setFromUser(fromUser);
        editRequest.setMessage(message);
        editRequest.setStatus(EditRequestStatus.PENDING);
        
        return editRequestRepository.save(editRequest);
    }
    
    public EditRequest getEditRequestById(Long requestId) {
        return editRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Edit request not found"));
    }
    
    public EditRequest approveEditRequest(Long requestId, User approvingUser) {
        EditRequest request = getEditRequestById(requestId);
        
        if (!request.getBook().getAuthor().getId().equals(approvingUser.getId())) {
            throw new RuntimeException("Only the book author can approve edit requests");
        }
        
        if (request.getStatus() != EditRequestStatus.PENDING) {
            throw new RuntimeException("This request has already been processed");
        }
        
        request.setStatus(EditRequestStatus.ACCEPTED);
        return editRequestRepository.save(request);
    }
    
    public EditRequest rejectEditRequest(Long requestId, User rejectingUser) {
        EditRequest request = getEditRequestById(requestId);
        
        if (!request.getBook().getAuthor().getId().equals(rejectingUser.getId())) {
            throw new RuntimeException("Only the book author can reject edit requests");
        }
        
        if (request.getStatus() != EditRequestStatus.PENDING) {
            throw new RuntimeException("This request has already been processed");
        }
        
        request.setStatus(EditRequestStatus.REJECTED);
        return editRequestRepository.save(request);
    }
    
    public void cancelEditRequest(Long requestId, User requester) {
        EditRequest request = getEditRequestById(requestId);
        
        if (!request.getFromUser().getId().equals(requester.getId())) {
            throw new RuntimeException("You can only cancel your own edit requests");
        }
        
        if (request.getStatus() != EditRequestStatus.PENDING) {
            throw new RuntimeException("Cannot cancel a processed request");
        }
        
        editRequestRepository.delete(request);
    }
    
    public List<EditRequest> getEditRequestsForBook(Book book) {
        return editRequestRepository.findByBook(book);
    }
    
    public List<EditRequest> getPendingEditRequestsForBook(Book book) {
        return editRequestRepository.findByBookAndStatus(book, EditRequestStatus.PENDING);
    }
    
    public List<EditRequest> getEditRequestsSentByUser(User user) {
        return editRequestRepository.findByFromUser(user);
    }
    
    public List<EditRequest> getPendingEditRequestsForAuthor(User author) {
        return editRequestRepository.findByBook_AuthorAndStatus(author, EditRequestStatus.PENDING);
    }
    
    public boolean hasEditAccessToBook(User user, Book book) {
        if (book.getAuthor().getId().equals(user.getId())) {
            return true;
        }
        
        List<EditRequest> userRequests = editRequestRepository.findByFromUser(user);
        return userRequests.stream()
                .anyMatch(request -> 
                    request.getBook().getId().equals(book.getId()) &&
                    request.getStatus() == EditRequestStatus.ACCEPTED
                );
    }
    
    public Long getTotalEditRequestCount() {
        return editRequestRepository.count();
    }
    
    public Long getEditRequestCountByStatus(EditRequestStatus status) {
        return editRequestRepository.countByStatus(status);
    }
    
    public Long getEditRequestCountForBook(Book book, EditRequestStatus status) {
        return editRequestRepository.countByBookAndStatus(book, status);
    }
}