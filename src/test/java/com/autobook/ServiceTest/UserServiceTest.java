package com.autobook.ServiceTest;

import com.autobook.Enum.PrivacyType;
import com.autobook.Exception.EmailAlreadyInUseException;
import com.autobook.Exception.UserNotFoundException;
import com.autobook.Exception.UsernameAlreadyExistsException;
import com.autobook.Factory.UserFactory;
import com.autobook.util.UserTestBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.autobook.Social.User.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserFactory userFactory;

    @InjectMocks
    private UserService userService;

    @Test
    void createUser_ok() {
        User requestUser = new UserTestBuilder()
                .withUsername("anton")
                .withVisibleName("Anton")
                .withEmail("anton@gmail.com")
                .withPassword("rawPassword")
                .build();

        User createdUser = new UserTestBuilder()
                .withUsername("anton")
                .withVisibleName("Anton")
                .withEmail("anton@gmail.com")
                .withPassword("encodedPassword")
                .build();

        when(userRepository.existsByEmail("anton@gmail.com")).thenReturn(false);
        when(userRepository.existsByUsername("anton")).thenReturn(false);
        when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPassword");
        when(userFactory.create("anton", "Anton", "anton@gmail.com", "encodedPassword"))
                .thenReturn(createdUser);
        when(userRepository.save(createdUser)).thenReturn(createdUser);

        User result = userService.createUser(requestUser);

        assertNotNull(result);
        assertEquals("anton", result.getUsername());
        assertEquals("Anton", result.getVisibleName());
        assertEquals("anton@gmail.com", result.getEmail());

        verify(userRepository).save(createdUser);
    }

    @Test
    void createUser_emailExists() {
        User requestUser = new UserTestBuilder()
                .withEmail("anton@gmail.com")
                .withUsername("anton")
                .build();

        when(userRepository.existsByEmail("anton@gmail.com")).thenReturn(true);

        assertThrows(EmailAlreadyInUseException.class, () -> userService.createUser(requestUser));

        verify(userRepository, never()).existsByUsername(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_usernameExists() {
        User requestUser = new UserTestBuilder()
                .withEmail("anton@gmail.com")
                .withUsername("anton")
                .build();

        when(userRepository.existsByEmail("anton@gmail.com")).thenReturn(false);
        when(userRepository.existsByUsername("anton")).thenReturn(true);

        assertThrows(UsernameAlreadyExistsException.class, () -> userService.createUser(requestUser));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUserById_ok() {
        User user = new UserTestBuilder()
                .withId(1L)
                .withUsername("anton")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getUserById(1L);

        assertEquals(1L, result.getId());
        assertEquals("anton", result.getUsername());
    }

    @Test
    void getUserById_notFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(1L));
    }

    @Test
    void updateBio_ok() {
        User user = new UserTestBuilder()
                .withId(1L)
                .withBio("old bio")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.updateBio(1L, "new bio");

        assertEquals("new bio", result.getBio());
        verify(userRepository).save(user);
    }

    @Test
    void updateVisibleName_ok() {
        User user = new UserTestBuilder()
                .withId(1L)
                .withVisibleName("Old Name")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.updateVisibleName(1L, "New Name");

        assertEquals("New Name", result.getVisibleName());
        verify(userRepository).save(user);
    }

    @Test
    void updatePrivacy_ok() {
        User user = new UserTestBuilder()
                .withId(1L)
                .withPrivacy(PrivacyType.PRIVATE)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.updatePrivacy(1L, PrivacyType.PUBLIC);

        assertEquals(PrivacyType.PUBLIC, result.getPrivacy());
        verify(userRepository).save(user);
    }

    @Test
    void updateProfileImage_ok() {
        User user = new UserTestBuilder()
                .withId(1L)
                .withProfileImage("old.png")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.updateProfileImage(1L, "new.png");

        assertEquals("new.png", result.getProfileImage());
        verify(userRepository).save(user);
    }

    @Test
    void deleteUser_ok() {
        User user = new UserTestBuilder()
                .withId(1L)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUserById(1L);

        verify(userRepository).delete(user);
    }
}