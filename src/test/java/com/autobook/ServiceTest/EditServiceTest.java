package com.autobook.ServiceTest;

import com.autobook.Enum.EditStatus;
import com.autobook.Exception.EditRequestAlreadyExistsException;
import com.autobook.Exception.EditRequestNotFoundException;
import com.autobook.Exception.InvalidEditRequestException;
import com.autobook.Factory.EditFactory;
import com.autobook.Library.Book.Book;
import com.autobook.Library.Edit.DTO.Request.CreateEditRequest;
import com.autobook.Library.Edit.DTO.Response.EditResponse;
import com.autobook.Library.Edit.Edit;
import com.autobook.Library.Edit.EditMapper;
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

    @Mock
    private EditMapper editMapper;

    @InjectMocks
    private EditService editService;

    @Test
    void createEditRequest_ok() {
        User author = new UserTestBuilder().withId(1L).build();
        User fromUser = new UserTestBuilder().withId(2L).withUsername("editor").build();
        Book book = new BookTestBuilder().withAuthor(author).build();

        CreateEditRequest request = new CreateEditRequest("I want to help edit this book");

        Edit edit = new EditTestBuilder()
                .withBook(book)
                .withFromUser(fromUser)
                .withMessage("I want to help edit this book")
                .withStatus(EditStatus.PENDING)
                .build();

        EditResponse response = mock(EditResponse.class);

        when(editRepository.findByBookAndStatus(book, EditStatus.PENDING)).thenReturn(List.of());
        when(editFactory.create(book, fromUser, request.message(), EditStatus.PENDING)).thenReturn(edit);
        when(editRepository.save(edit)).thenReturn(edit);
        when(editMapper.toResponse(edit)).thenReturn(response);

        EditResponse result = editService.createEditRequest(book, fromUser, request);

        assertNotNull(result);
        assertEquals(response, result);

        verify(editRepository).findByBookAndStatus(book, EditStatus.PENDING);
        verify(editFactory).create(book, fromUser, request.message(), EditStatus.PENDING);
        verify(editRepository).save(edit);
        verify(editMapper).toResponse(edit);
    }

    @Test
    void createEditRequest_bookIsNull() {
        User fromUser = new UserTestBuilder().withId(2L).build();
        CreateEditRequest request = new CreateEditRequest("message");

        assertThrows(InvalidEditRequestException.class,
                () -> editService.createEditRequest(null, fromUser, request));

        verify(editRepository, never()).findByBookAndStatus(any(), any());
        verify(editFactory, never()).create(any(), any(), any(), any());
        verify(editRepository, never()).save(any());
        verify(editMapper, never()).toResponse(any());
    }

    @Test
    void createEditRequest_userIsNull() {
        Book book = new BookTestBuilder().build();
        CreateEditRequest request = new CreateEditRequest("message");

        assertThrows(InvalidEditRequestException.class,
                () -> editService.createEditRequest(book, null, request));

        verify(editRepository, never()).findByBookAndStatus(any(), any());
        verify(editFactory, never()).create(any(), any(), any(), any());
        verify(editRepository, never()).save(any());
        verify(editMapper, never()).toResponse(any());
    }

    @Test
    void createEditRequest_editForOwnBook() {
        User author = new UserTestBuilder().withId(1L).build();
        Book book = new BookTestBuilder().withAuthor(author).build();
        User sameUser = new UserTestBuilder().withId(1L).build();
        CreateEditRequest request = new CreateEditRequest("message");

        assertThrows(InvalidEditRequestException.class,
                () -> editService.createEditRequest(book, sameUser, request));

        verify(editRepository, never()).findByBookAndStatus(any(), any());
        verify(editFactory, never()).create(any(), any(), any(), any());
        verify(editRepository, never()).save(any());
        verify(editMapper, never()).toResponse(any());
    }

    @Test
    void createEditRequest_editAlreadyExists() {
        User author = new UserTestBuilder().withId(1L).build();
        User fromUser = new UserTestBuilder().withId(2L).build();
        Book book = new BookTestBuilder().withAuthor(author).build();
        CreateEditRequest request = new CreateEditRequest("message");

        Edit existingEdit = new EditTestBuilder()
                .withBook(book)
                .withFromUser(fromUser)
                .withStatus(EditStatus.PENDING)
                .build();

        when(editRepository.findByBookAndStatus(book, EditStatus.PENDING)).thenReturn(List.of(existingEdit));

        assertThrows(EditRequestAlreadyExistsException.class,
                () -> editService.createEditRequest(book, fromUser, request));

        verify(editRepository).findByBookAndStatus(book, EditStatus.PENDING);
        verify(editFactory, never()).create(any(), any(), any(), any());
        verify(editRepository, never()).save(any());
        verify(editMapper, never()).toResponse(any());
    }

    @Test
    void getEditRequestById_ok() {
        Edit edit = new EditTestBuilder().withId(10L).build();
        EditResponse response = mock(EditResponse.class);

        when(editRepository.findById(10L)).thenReturn(Optional.of(edit));
        when(editMapper.toResponse(edit)).thenReturn(response);

        EditResponse result = editService.getEditRequestById(10L);

        assertNotNull(result);
        assertEquals(response, result);

        verify(editRepository).findById(10L);
        verify(editMapper).toResponse(edit);
    }

    @Test
    void getEditRequestById_notFound() {
        when(editRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(EditRequestNotFoundException.class,
                () -> editService.getEditRequestById(10L));

        verify(editRepository).findById(10L);
        verify(editMapper, never()).toResponse(any());
    }

    @Test
    void getEditRequestsByBook_ok() {
        Book book = new BookTestBuilder().build();

        Edit edit1 = new EditTestBuilder().withBook(book).build();
        Edit edit2 = new EditTestBuilder().withId(2L).withBook(book).build();

        EditResponse response1 = mock(EditResponse.class);
        EditResponse response2 = mock(EditResponse.class);

        when(editRepository.findByBook(book)).thenReturn(List.of(edit1, edit2));
        when(editMapper.toResponse(edit1)).thenReturn(response1);
        when(editMapper.toResponse(edit2)).thenReturn(response2);

        List<EditResponse> result = editService.getEditRequestsByBook(book);

        assertEquals(2, result.size());
        assertEquals(response1, result.get(0));
        assertEquals(response2, result.get(1));

        verify(editRepository).findByBook(book);
        verify(editMapper).toResponse(edit1);
        verify(editMapper).toResponse(edit2);
    }

    @Test
    void getPendingEditRequestsByBook_ok() {
        Book book = new BookTestBuilder().build();
        Edit edit = new EditTestBuilder()
                .withBook(book)
                .withStatus(EditStatus.PENDING)
                .build();

        EditResponse response = mock(EditResponse.class);

        when(editRepository.findByBookAndStatus(book, EditStatus.PENDING)).thenReturn(List.of(edit));
        when(editMapper.toResponse(edit)).thenReturn(response);

        List<EditResponse> result = editService.getPendingEditRequestsByBook(book);

        assertEquals(1, result.size());
        assertEquals(response, result.get(0));

        verify(editRepository).findByBookAndStatus(book, EditStatus.PENDING);
        verify(editMapper).toResponse(edit);
    }

    @Test
    void getEditRequestsByUser_ok() {
        User user = new UserTestBuilder().build();
        Edit edit = new EditTestBuilder().withFromUser(user).build();
        EditResponse response = mock(EditResponse.class);

        when(editRepository.findByFromUser(user)).thenReturn(List.of(edit));
        when(editMapper.toResponse(edit)).thenReturn(response);

        List<EditResponse> result = editService.getEditRequestsByUser(user);

        assertEquals(1, result.size());
        assertEquals(response, result.get(0));

        verify(editRepository).findByFromUser(user);
        verify(editMapper).toResponse(edit);
    }

    @Test
    void getPendingEditRequestsByUser_ok() {
        User user = new UserTestBuilder().build();
        Edit edit = new EditTestBuilder()
                .withFromUser(user)
                .withStatus(EditStatus.PENDING)
                .build();

        EditResponse response = mock(EditResponse.class);

        when(editRepository.findByFromUserAndStatus(user, EditStatus.PENDING)).thenReturn(List.of(edit));
        when(editMapper.toResponse(edit)).thenReturn(response);

        List<EditResponse> result = editService.getPendingEditRequestsByUser(user);

        assertEquals(1, result.size());
        assertEquals(response, result.get(0));

        verify(editRepository).findByFromUserAndStatus(user, EditStatus.PENDING);
        verify(editMapper).toResponse(edit);
    }

    @Test
    void getReceivedEditRequests_ok() {
        User author = new UserTestBuilder().build();

        Edit edit1 = new EditTestBuilder().build();
        Edit edit2 = new EditTestBuilder().withId(2L).build();

        EditResponse response1 = mock(EditResponse.class);
        EditResponse response2 = mock(EditResponse.class);

        when(editRepository.findByBook_Author(author)).thenReturn(List.of(edit1, edit2));
        when(editMapper.toResponse(edit1)).thenReturn(response1);
        when(editMapper.toResponse(edit2)).thenReturn(response2);

        List<EditResponse> result = editService.getReceivedEditRequests(author);

        assertEquals(2, result.size());
        assertEquals(response1, result.get(0));
        assertEquals(response2, result.get(1));

        verify(editRepository).findByBook_Author(author);
        verify(editMapper).toResponse(edit1);
        verify(editMapper).toResponse(edit2);
    }

    @Test
    void getReceivedPendingEditRequests_ok() {
        User author = new UserTestBuilder().build();
        Edit edit = new EditTestBuilder()
                .withStatus(EditStatus.PENDING)
                .build();

        EditResponse response = mock(EditResponse.class);

        when(editRepository.findByBook_AuthorAndStatus(author, EditStatus.PENDING)).thenReturn(List.of(edit));
        when(editMapper.toResponse(edit)).thenReturn(response);

        List<EditResponse> result = editService.getReceivedPendingEditRequests(author);

        assertEquals(1, result.size());
        assertEquals(response, result.get(0));

        verify(editRepository).findByBook_AuthorAndStatus(author, EditStatus.PENDING);
        verify(editMapper).toResponse(edit);
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

        EditResponse response = mock(EditResponse.class);

        when(editRepository.findById(1L)).thenReturn(Optional.of(edit));
        when(editRepository.save(edit)).thenReturn(edit);
        when(editMapper.toResponse(edit)).thenReturn(response);

        EditResponse result = editService.acceptEditRequest(1L);

        assertNotNull(result);
        assertEquals(response, result);
        assertEquals(EditStatus.ACCEPTED, edit.getStatus());

        verify(editRepository).findById(1L);
        verify(editRepository).save(edit);
        verify(editMapper).toResponse(edit);
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
        verify(editMapper, never()).toResponse(any());
    }

    @Test
    void rejectEditRequest_ok() {
        Edit edit = new EditTestBuilder()
                .withId(1L)
                .withStatus(EditStatus.PENDING)
                .build();

        EditResponse response = mock(EditResponse.class);

        when(editRepository.findById(1L)).thenReturn(Optional.of(edit));
        when(editRepository.save(edit)).thenReturn(edit);
        when(editMapper.toResponse(edit)).thenReturn(response);

        EditResponse result = editService.rejectEditRequest(1L);

        assertNotNull(result);
        assertEquals(response, result);
        assertEquals(EditStatus.REJECTED, edit.getStatus());

        verify(editRepository).findById(1L);
        verify(editRepository).save(edit);
        verify(editMapper).toResponse(edit);
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
        verify(editMapper, never()).toResponse(any());
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