package com.autobook.ControllerTest;

import com.autobook.Library.Book.Book;
import com.autobook.Library.Book.BookRepository;
import com.autobook.Library.BookComment.BookCommentController;
import com.autobook.Library.BookComment.BookCommentService;
import com.autobook.Library.BookComment.DTO.Request.CreateBookCommentRequest;
import com.autobook.Social.User.User;
import com.autobook.Social.User.UserRepository;
import com.autobook.util.UserTestBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Principal;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookCommentControllerTest {

    @Mock
    private BookCommentService bookCommentService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private Principal principal;

    @InjectMocks
    private BookCommentController bookCommentController;

    @Test
    void createComment() {
        User user = new UserTestBuilder().withId(1L).withUsername("user").build();
        Book book = new Book();
        book.setId(10L);
        CreateBookCommentRequest request = new CreateBookCommentRequest("Content", null);

        when(principal.getName()).thenReturn("user");
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));

        bookCommentController.createComment(10L, request, principal);

        verify(bookCommentService).createComment(request, user, book);
    }
}
