package com.autobook.ControllerTest;

import com.autobook.Library.SavedItem.SavedItemController;
import com.autobook.Library.SavedItem.SavedItemService;
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
class SavedItemControllerTest {

    @Mock
    private SavedItemService savedItemService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private Principal principal;

    @InjectMocks
    private SavedItemController savedItemController;

    @Test
    void toggleSaveBook() {
        User user = new UserTestBuilder().withId(1L).withUsername("user").build();
        when(principal.getName()).thenReturn("user");
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));

        savedItemController.toggleSaveBook(principal, 10L);

        verify(savedItemService).toggleSaveBook(user, 10L);
    }

    @Test
    void getMySavedItems() {
        User user = new UserTestBuilder().withId(1L).withUsername("user").build();
        when(principal.getName()).thenReturn("user");
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));

        savedItemController.getMySavedItems(principal);

        verify(savedItemService).getMySavedItems(user);
    }
}
