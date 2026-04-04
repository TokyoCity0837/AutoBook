package com.autobook.ServiceTest;

import com.autobook.Enum.EditStatus;
import com.autobook.Exception.EditRequestAlreadyExistsException;
import com.autobook.Exception.EditRequestNotFoundException;
import com.autobook.Exception.InvalidEditRequestException;
import com.autobook.Factory.EditFactory;
import com.autobook.Library.Book.Book;
import com.autobook.Library.Edit.Edit;
import com.autobook.Library.Edit.EditRepository;
import com.autobook.Library.Edit.EditService;
import com.autobook.Social.User.User;
import com.autobook.util.BookTestBuilder;
import com.autobook.util.EditTestBuilder;
import com.autobook.util.UserTestBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EditServiceTest {

    @Mock
    private EditRepository editRepository;

    @Mock
    private EditFactory editFactory;

    @InjectMocks
    private EditService editService;

    @Test
    void createEditRequest_ok() {
        User author = new UserTestBuilder().withId(1L).build();
        User fromUser = new UserTestBuilder().withId(2L).withUsername("editor").build();
        Book book = new BookTestBuilder().withAuthor(author).build();

        Edit edit = new EditTestBuilder()
                .withBook(book)
                .withFromUser(fromUser)
                .withMessage("I want to help edit this book")
                .withStatus(EditStatus.PENDING)
                .build();

        when(editRepository.findByBookAndStatus(book, EditStatus.PENDING)).thenReturn(List.of());
        when(editFactory.create(book, fromUser, "I want to help edit this book", EditStatus.PENDING)).thenReturn(edit);
        when(editRepository.save(edit)).thenReturn(edit);

        Edit result = editService.createEditRequest(book, fromUser, "I want to help edit this book");

        assertNotNull(result);
        assertEquals(book, result.getBook());
        assertEquals(fromUser, result.getFromUser());
        assertEquals("I want to help edit this book", result.getMessage());
        assertEquals(EditStatus.PENDING, result.getStatus());

        verify(editRepository).findByBookAndStatus(book, EditStatus.PENDING);
        verify(editFactory).create(book, fromUser, "I want to help edit this book", EditStatus.PENDING);
        verify(editRepository).save(edit);
    }

    @Test
    void createEditRequest_bookIsNull() {
        User fromUser = new UserTestBuilder().withId(2L).build();

        assertThrows(InvalidEditRequestException.class,
                () -> editService.createEditRequest(null, fromUser, "message"));

        verify(editRepository, never()).findByBookAndStatus(any(), any());
        verify(editFactory, never()).create(any(), any(), any(), any());
        verify(editRepository, never()).save(any());
    }

    @Test
    void createEditRequest_userIsNull() {
        Book book = new BookTestBuilder().build();

        assertThrows(InvalidEditRequestException.class,
                () -> editService.createEditRequest(book, null, "message"));

        verify(editRepository, never()).findByBookAndStatus(any(), any());
        verify(editFactory, never()).create(any(), any(), any(), any());
        verify(editRepository, never()).save(any());
    }

    @Test
    void createEditRequest_editForOwnBook() {
        User author = new UserTestBuilder().withId(1L).build();
        Book book = new BookTestBuilder().withAuthor(author).build();
        User sameUser = new UserTestBuilder().withId(1L).build();

        assertThrows(InvalidEditRequestException.class,
                () -> editService.createEditRequest(book, sameUser, "message"));

        verify(editRepository, never()).findByBookAndStatus(any(), any());
        verify(editFactory, never()).create(any(), any(), any(), any());
        verify(editRepository, never()).save(any());
    }

    @Test
    void createEditRequest_editAlreadyExists() {
        User author = new UserTestBuilder().withId(1L).build();
        User fromUser = new UserTestBuilder().withId(2L).build();
        Book book = new BookTestBuilder().withAuthor(author).build();

        Edit existingEdit = new EditTestBuilder()
                .withBook(book)
                .withFromUser(fromUser)
                .withStatus(EditStatus.PENDING)
                .build();

        when(editRepository.findByBookAndStatus(book, EditStatus.PENDING)).thenReturn(List.of(existingEdit));

        assertThrows(EditRequestAlreadyExistsException.class,
                () -> editService.createEditRequest(book, fromUser, "message"));

        verify(editRepository).findByBookAndStatus(book, EditStatus.PENDING);
        verify(editFactory, never()).create(any(), any(), any(), any());
        verify(editRepository, never()).save(any());
    }

    @Test
    void getEditRequestById_ok() {
        Edit edit = new EditTestBuilder().withId(10L).build();

        when(editRepository.findById(10L)).thenReturn(Optional.of(edit));

        Edit result = editService.getEditRequestById(10L);

        assertNotNull(result);
        assertEquals(10L, result.getId());

        verify(editRepository).findById(10L);
    }

    @Test
    void getEditRequestById_notFound() {
        when(editRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(EditRequestNotFoundException.class,
                () -> editService.getEditRequestById(10L));

        verify(editRepository).findById(10L);
    }

    @Test
    void getEditRequestsByBook_ok() {
        Book book = new BookTestBuilder().build();
        List<Edit> edits = List.of(
                new EditTestBuilder().withBook(book).build(),
                new EditTestBuilder().withId(2L).withBook(book).build()
        );

        when(editRepository.findByBook(book)).thenReturn(edits);

        List<Edit> result = editService.getEditRequestsByBook(book);

        assertEquals(2, result.size());
        verify(editRepository).findByBook(book);
    }

    @Test
    void getPendingEditRequestsByBook_ok() {
        Book book = new BookTestBuilder().build();
        List<Edit> edits = List.of(
                new EditTestBuilder().withBook(book).withStatus(EditStatus.PENDING).build()
        );

        when(editRepository.findByBookAndStatus(book, EditStatus.PENDING)).thenReturn(edits);

        List<Edit> result = editService.getPendingEditRequestsByBook(book);

        assertEquals(1, result.size());
        verify(editRepository).findByBookAndStatus(book, EditStatus.PENDING);
    }

    @Test
    void getEditRequestsByUser_ok() {
        User user = new UserTestBuilder().build();
        List<Edit> edits = List.of(
                new EditTestBuilder().withFromUser(user).build()
        );

        when(editRepository.findByFromUser(user)).thenReturn(edits);

        List<Edit> result = editService.getEditRequestsByUser(user);

        assertEquals(1, result.size());
        verify(editRepository).findByFromUser(user);
    }

    @Test
    void getPendingEditRequestsByUser_ok() {
        User user = new UserTestBuilder().build();
        List<Edit> edits = List.of(
                new EditTestBuilder().withFromUser(user).withStatus(EditStatus.PENDING).build()
        );

        when(editRepository.findByFromUserAndStatus(user, EditStatus.PENDING)).thenReturn(edits);

        List<Edit> result = editService.getPendingEditRequestsByUser(user);

        assertEquals(1, result.size());
        verify(editRepository).findByFromUserAndStatus(user, EditStatus.PENDING);
    }

    @Test
    void getReceivedEditRequests_ok() {
        User author = new UserTestBuilder().build();
        List<Edit> edits = List.of(
                new EditTestBuilder().build(),
                new EditTestBuilder().withId(2L).build()
        );

        when(editRepository.findByBook_Author(author)).thenReturn(edits);

        List<Edit> result = editService.getReceivedEditRequests(author);

        assertEquals(2, result.size());
        verify(editRepository).findByBook_Author(author);
    }

    @Test
    void getReceivedPendingEditRequests_ok() {
        User author = new UserTestBuilder().build();
        List<Edit> edits = List.of(
                new EditTestBuilder().withStatus(EditStatus.PENDING).build()
        );

        when(editRepository.findByBook_AuthorAndStatus(author, EditStatus.PENDING)).thenReturn(edits);

        List<Edit> result = editService.getReceivedPendingEditRequests(author);

        assertEquals(1, result.size());
        verify(editRepository).findByBook_AuthorAndStatus(author, EditStatus.PENDING);
    }

    @Test
    void countPendingRequests_ok() {
        when(editRepository.countByStatus(EditStatus.PENDING)).thenReturn(5L);

        Long result = editService.countPendingRequests();

        assertEquals(5L, result);
        verify(editRepository).countByStatus(EditStatus.PENDING);
    }

    @Test
    void countPendingRequestsByBook_ok() {
        Book book = new BookTestBuilder().build();

        when(editRepository.countByBookAndStatus(book, EditStatus.PENDING)).thenReturn(3L);

        Long result = editService.countPendingRequestsByBook(book);

        assertEquals(3L, result);
        verify(editRepository).countByBookAndStatus(book, EditStatus.PENDING);
    }

    @Test
    void acceptEditRequest_ok() {
        Edit edit = new EditTestBuilder()
                .withId(1L)
                .withStatus(EditStatus.PENDING)
                .build();

        when(editRepository.findById(1L)).thenReturn(Optional.of(edit));
        when(editRepository.save(edit)).thenReturn(edit);

        Edit result = editService.acceptEditRequest(1L);

        assertNotNull(result);
        assertEquals(EditStatus.ACCEPTED, result.getStatus());

        verify(editRepository).findById(1L);
        verify(editRepository).save(edit);
    }

    @Test
    void acceptEditRequest_isNotPending() {
        Edit edit = new EditTestBuilder()
                .withId(1L)
                .withStatus(EditStatus.ACCEPTED)
                .build();

        when(editRepository.findById(1L)).thenReturn(Optional.of(edit));

        assertThrows(InvalidEditRequestException.class,
                () -> editService.acceptEditRequest(1L));

        verify(editRepository).findById(1L);
        verify(editRepository, never()).save(any());
    }

    @Test
    void rejectEditRequest_ok() {
        Edit edit = new EditTestBuilder()
                .withId(1L)
                .withStatus(EditStatus.PENDING)
                .build();

        when(editRepository.findById(1L)).thenReturn(Optional.of(edit));
        when(editRepository.save(edit)).thenReturn(edit);

        Edit result = editService.rejectEditRequest(1L);

        assertNotNull(result);
        assertEquals(EditStatus.REJECTED, result.getStatus());

        verify(editRepository).findById(1L);
        verify(editRepository).save(edit);
    }

    @Test
    void rejectEditRequest_isNotPending() {
        Edit edit = new EditTestBuilder()
                .withId(1L)
                .withStatus(EditStatus.REJECTED)
                .build();

        when(editRepository.findById(1L)).thenReturn(Optional.of(edit));

        assertThrows(InvalidEditRequestException.class,
                () -> editService.rejectEditRequest(1L));

        verify(editRepository).findById(1L);
        verify(editRepository, never()).save(any());
    }

    @Test
    void deleteEditRequest_ok() {
        Edit edit = new EditTestBuilder().withId(1L).build();

        when(editRepository.findById(1L)).thenReturn(Optional.of(edit));

        editService.deleteEditRequest(1L);

        verify(editRepository).findById(1L);
        verify(editRepository).delete(edit);
    }
}