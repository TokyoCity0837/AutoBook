package com.autobook.ControllerTest;

import com.autobook.Library.Book.Book;
import com.autobook.Library.Book.BookRepository;
import com.autobook.Library.Edit.DTO.Request.CreateEditRequest;
import com.autobook.Library.Edit.DTO.Response.EditResponse;
import com.autobook.Library.Edit.EditController;
import com.autobook.Library.Edit.EditService;
import com.autobook.Social.User.User;
import com.autobook.Social.User.UserRepository;
import com.autobook.util.BookTestBuilder;
import com.autobook.util.UserTestBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EditControllerTest {

    @Mock
    private EditService editService;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private EditController editController;

    private Principal createPrincipal(String name) {
        return () -> name;
    }

    @Test
    void createEditRequest() {
        Principal p = createPrincipal("user");
        User user = new UserTestBuilder().withId(1L).withUsername("user").build();
        Book book = new BookTestBuilder().withId(1L).build();
        
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        CreateEditRequest req = new CreateEditRequest("msg");
        EditResponse resp = mock(EditResponse.class);
        when(editService.createEditRequest(book, user, req)).thenReturn(resp);

        assertEquals(resp, editController.createEditRequest(1L, req, p));
    }

    @Test
    void getEditRequest() {
        EditResponse resp = mock(EditResponse.class);
        when(editService.getEditRequestById(1L)).thenReturn(resp);
        assertEquals(resp, editController.getEditRequest(1L));
    }

    @Test
    void getEditRequestsByBook() {
        Book book = new BookTestBuilder().withId(1L).build();
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(editService.getEditRequestsByBook(book)).thenReturn(List.of());
        assertEquals(0, editController.getEditRequestsByBook(1L).size());
    }

    @Test
    void getPendingEditRequestsByBook() {
        Book book = new BookTestBuilder().withId(1L).build();
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(editService.getPendingEditRequestsByBook(book)).thenReturn(List.of());
        assertEquals(0, editController.getPendingEditRequestsByBook(1L).size());
    }

    @Test
    void getMyEditRequests() {
        Principal p = createPrincipal("user");
        User user = new UserTestBuilder().withId(1L).withUsername("user").build();
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(editService.getEditRequestsByUser(user)).thenReturn(List.of());
        assertEquals(0, editController.getMyEditRequests(p).size());
    }

    @Test
    void getMyPendingEditRequests() {
        Principal p = createPrincipal("user");
        User user = new UserTestBuilder().withId(1L).withUsername("user").build();
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(editService.getPendingEditRequestsByUser(user)).thenReturn(List.of());
        assertEquals(0, editController.getMyPendingEditRequests(p).size());
    }

    @Test
    void getReceivedEditRequests() {
        Principal p = createPrincipal("user");
        User user = new UserTestBuilder().withId(1L).withUsername("user").build();
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(editService.getReceivedEditRequests(user)).thenReturn(List.of());
        assertEquals(0, editController.getReceivedEditRequests(p).size());
    }

    @Test
    void getReceivedPendingEditRequests() {
        Principal p = createPrincipal("user");
        User user = new UserTestBuilder().withId(1L).withUsername("user").build();
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(editService.getReceivedPendingEditRequests(user)).thenReturn(List.of());
        assertEquals(0, editController.getReceivedPendingEditRequests(p).size());
    }

    @Test
    void acceptEditRequest() {
        EditResponse resp = mock(EditResponse.class);
        when(editService.acceptEditRequest(1L)).thenReturn(resp);
        assertEquals(resp, editController.acceptEditRequest(1L));
    }

    @Test
    void rejectEditRequest() {
        EditResponse resp = mock(EditResponse.class);
        when(editService.rejectEditRequest(1L)).thenReturn(resp);
        assertEquals(resp, editController.rejectEditRequest(1L));
    }

    @Test
    void deleteEditRequest() {
        editController.deleteEditRequest(1L);
        verify(editService).deleteEditRequest(1L);
    }
}
