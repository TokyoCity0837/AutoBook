package com.autobook.ServiceTest;

import com.autobook.Enum.PostType;
import com.autobook.Enum.PrivacyType;
import com.autobook.Enum.UserRole;
import com.autobook.Exception.EmailAlreadyInUseException;
import com.autobook.Exception.UserNotFoundException;
import com.autobook.Exception.UsernameAlreadyExistsException;
import com.autobook.Factory.UserFactory;
import com.autobook.Library.Book.BookMapper;
import com.autobook.Library.Book.BookRepository;
import com.autobook.Library.Book.DTO.Response.BookCardResponse;
import com.autobook.Social.Post.PostMapper;
import com.autobook.Social.Post.PostRepository;
import com.autobook.Social.Post.DTO.Response.PostResponse;
import com.autobook.Social.User.DTO.Request.UserRegisterRequest;
import com.autobook.Social.User.DTO.Request.UserUpdateRequest;
import com.autobook.Social.User.DTO.Response.UserCardResponse;
import com.autobook.Social.User.DTO.Response.UserProfileResponse;
import com.autobook.Social.User.User;
import com.autobook.Social.User.UserMapper;
import com.autobook.Social.User.UserRepository;
import com.autobook.Social.User.UserService;
import com.autobook.util.UserTestBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserFactory userFactory;

    @Mock
    private UserMapper userMapper;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostMapper postMapper;

    @InjectMocks
    private UserService userService;

    @Test
    void createUser_ok() {
        UserRegisterRequest request = new UserRegisterRequest(
                "anton",
                "Anton",
                "anton@gmail.com",
                "rawPassword"
        );

        User createdUser = new UserTestBuilder()
                .withId(1L)
                .withUsername("anton")
                .withVisibleName("Anton")
                .withEmail("anton@gmail.com")
                .withPassword("encodedPassword")
                .build();

        List<BookCardResponse> books = List.of();
        List<PostResponse> posts = List.of();

        UserProfileResponse response = new UserProfileResponse(
                1L,
                "anton",
                "Anton",
                createdUser.getBio(),
                createdUser.getProfileImage(),
                createdUser.getPrivacy(),
                createdUser.getCreatedAt(),
                createdUser.getRole(),
                books,
                posts
        );

        when(userRepository.existsByEmail("anton@gmail.com")).thenReturn(false);
        when(userRepository.existsByUsername("anton")).thenReturn(false);
        when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPassword");
        when(userFactory.create("anton", "Anton", "anton@gmail.com", "encodedPassword"))
                .thenReturn(createdUser);
        when(userRepository.save(createdUser)).thenReturn(createdUser);

        when(bookRepository.findByAuthor(createdUser)).thenReturn(List.of());
        when(postRepository.findByAuthorOrderByCreatedAtDesc(createdUser)).thenReturn(List.of());

        when(userMapper.toProfileResponse(createdUser, books, posts)).thenReturn(response);

        UserProfileResponse result = userService.createUser(request);

        assertNotNull(result);
        assertEquals("anton", result.username());
        assertEquals("Anton", result.visibleName());
        assertTrue(result.books().isEmpty());
        assertTrue(result.posts().isEmpty());

        verify(userRepository).save(createdUser);
        verify(bookRepository).findByAuthor(createdUser);
        verify(postRepository).findByAuthorOrderByCreatedAtDesc(createdUser);
        verify(userMapper).toProfileResponse(createdUser, books, posts);
    }

    @Test
    void createUser_emailExists() {
        UserRegisterRequest request = new UserRegisterRequest(
                "anton",
                "Anton",
                "anton@gmail.com",
                "rawPassword"
        );

        when(userRepository.existsByEmail("anton@gmail.com")).thenReturn(true);

        assertThrows(EmailAlreadyInUseException.class, () -> userService.createUser(request));

        verify(userRepository, never()).existsByUsername(anyString());
        verify(userRepository, never()).save(any(User.class));
        verify(userMapper, never()).toProfileResponse(any(), any(), any());
    }

    @Test
    void createUser_usernameExists() {
        UserRegisterRequest request = new UserRegisterRequest(
                "anton",
                "Anton",
                "anton@gmail.com",
                "rawPassword"
        );

        when(userRepository.existsByEmail("anton@gmail.com")).thenReturn(false);
        when(userRepository.existsByUsername("anton")).thenReturn(true);

        assertThrows(UsernameAlreadyExistsException.class, () -> userService.createUser(request));

        verify(userRepository, never()).save(any(User.class));
        verify(userMapper, never()).toProfileResponse(any(), any(), any());
    }

    @Test
    void getUserProfileById_ok() {
        User user = new UserTestBuilder()
                .withId(1L)
                .withUsername("anton")
                .withVisibleName("Anton")
                .build();

                BookCardResponse bookResponse = new BookCardResponse(
                10L,
                "My Book",
                "cover.png",
                new UserCardResponse(1L, "Anton", "user.png", UserRole.USER)
        );

        PostResponse postResponse = new PostResponse(
                20L,
                "Hello world",
                new UserCardResponse(1L, "Anton", "user.png", UserRole.USER),
                PostType.FEED,
                LocalDateTime.now(),
                LocalDateTime.now(),
                0,
                0,
                0
        );

        List<BookCardResponse> books = List.of(bookResponse);
        List<PostResponse> posts = List.of(postResponse);

        UserProfileResponse response = new UserProfileResponse(
                1L,
                "anton",
                "Anton",
                user.getBio(),
                user.getProfileImage(),
                user.getPrivacy(),
                user.getCreatedAt(),
                user.getRole(),
                books,
                posts
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookRepository.findByAuthor(user)).thenReturn(List.of());
        when(postRepository.findByAuthorOrderByCreatedAtDesc(user)).thenReturn(List.of());
        when(userMapper.toProfileResponse(user, books, posts)).thenReturn(response);

        when(bookRepository.findByAuthor(user)).thenReturn(List.of(mock(com.autobook.Library.Book.Book.class)));
        when(postRepository.findByAuthorOrderByCreatedAtDesc(user)).thenReturn(List.of(mock(com.autobook.Social.Post.Post.class)));

        when(bookMapper.toCardResponse(any())).thenReturn(bookResponse);
        when(postMapper.toResponse(any())).thenReturn(postResponse);

        UserProfileResponse result = userService.getUserProfileById(1L);

        assertEquals(1L, result.id());
        assertEquals("anton", result.username());
        assertEquals("Anton", result.visibleName());
        assertEquals(1, result.books().size());
        assertEquals(1, result.posts().size());
    }

    @Test
    void getUserProfileById_notFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserProfileById(1L));
    }

    @Test
    void getUserProfileByUsername_ok() {
        User user = new UserTestBuilder()
                .withId(1L)
                .withUsername("anton")
                .withVisibleName("Anton")
                .build();

        List<BookCardResponse> books = List.of();
        List<PostResponse> posts = List.of();

        UserProfileResponse response = new UserProfileResponse(
                1L,
                "anton",
                "Anton",
                user.getBio(),
                user.getProfileImage(),
                user.getPrivacy(),
                user.getCreatedAt(),
                user.getRole(),
                books,
                posts
        );

        when(userRepository.findByUsername("anton")).thenReturn(Optional.of(user));
        when(bookRepository.findByAuthor(user)).thenReturn(List.of());
        when(postRepository.findByAuthorOrderByCreatedAtDesc(user)).thenReturn(List.of());
        when(userMapper.toProfileResponse(user, books, posts)).thenReturn(response);

        UserProfileResponse result = userService.getUserProfileByUsername("anton");

        assertEquals("anton", result.username());
        assertEquals("Anton", result.visibleName());
    }

    @Test
    void getUserProfileByUsername_notFound() {
        when(userRepository.findByUsername("anton")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserProfileByUsername("anton"));
    }

    @Test
    void getAllUsers_ok() {
        User user1 = new UserTestBuilder()
                .withId(2L)
                .withVisibleName("Anton")
                .withProfileImage("anton.png")
                .withRole(UserRole.USER)
                .build();

        User user2 = new UserTestBuilder()
                .withId(3L)
                .withVisibleName("Admin")
                .withProfileImage("admin.png")
                .withRole(UserRole.ADMIN)
                .build();

        UserCardResponse response1 = new UserCardResponse(2L, "Anton", "anton.png", UserRole.USER);
        UserCardResponse response2 = new UserCardResponse(3L, "Admin", "admin.png", UserRole.ADMIN);

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));
        when(userMapper.toCardResponse(user1)).thenReturn(response1);
        when(userMapper.toCardResponse(user2)).thenReturn(response2);

        List<UserCardResponse> result = userService.getAllUsers();

        assertEquals(2, result.size());
        assertEquals("Anton", result.get(0).visibleName());
        assertEquals("Admin", result.get(1).visibleName());
    }

    @Test
    void getUsersByRole_ok() {
        User user = new UserTestBuilder()
                .withId(1L)
                .withRole(UserRole.ADMIN)
                .withVisibleName("Admin")
                .withProfileImage("admin.png")
                .build();

        UserCardResponse response = new UserCardResponse(1L, "Admin", "admin.png", UserRole.ADMIN);

        when(userRepository.findByRole(UserRole.ADMIN)).thenReturn(List.of(user));
        when(userMapper.toCardResponse(user)).thenReturn(response);

        List<UserCardResponse> result = userService.getUsersByRole(UserRole.ADMIN);

        assertEquals(1, result.size());
        assertEquals(UserRole.ADMIN, result.get(0).userRole());
    }

    @Test
    void searchUsersByUsername_ok() {
        User user = new UserTestBuilder()
                .withId(1L)
                .withUsername("anton")
                .withVisibleName("Anton")
                .withProfileImage("anton.png")
                .build();

        UserCardResponse response = new UserCardResponse(1L, "Anton", "anton.png", UserRole.USER);

        when(userRepository.findByUsernameContainingIgnoreCase("ant")).thenReturn(List.of(user));
        when(userMapper.toCardResponse(user)).thenReturn(response);

        List<UserCardResponse> result = userService.searchUsersByUsername("ant");

        assertEquals(1, result.size());
        assertEquals("Anton", result.get(0).visibleName());
    }

    @Test
    void getUsersByPrivacy_ok() {
        User user = new UserTestBuilder()
                .withId(1L)
                .withPrivacy(PrivacyType.PUBLIC)
                .withVisibleName("Anton")
                .withProfileImage("anton.png")
                .build();

        UserCardResponse response = new UserCardResponse(1L, "Anton", "anton.png", UserRole.USER);

        when(userRepository.findByPrivacy(PrivacyType.PUBLIC)).thenReturn(List.of(user));
        when(userMapper.toCardResponse(user)).thenReturn(response);

        List<UserCardResponse> result = userService.getUsersByPrivacy(PrivacyType.PUBLIC);

        assertEquals(1, result.size());
        assertEquals("Anton", result.get(0).visibleName());
    }

    @Test
    void countUsersByPrivacy_ok() {
        when(userRepository.countByPrivacy(PrivacyType.PUBLIC)).thenReturn(5L);

        Long result = userService.countUsersByPrivacy(PrivacyType.PUBLIC);

        assertEquals(5L, result);
    }

    @Test
    void existsByUsername_ok() {
        when(userRepository.existsByUsername("anton")).thenReturn(true);

        boolean result = userService.existsByUsername("anton");

        assertTrue(result);
    }

    @Test
    void existsByEmail_ok() {
        when(userRepository.existsByEmail("anton@gmail.com")).thenReturn(true);

        boolean result = userService.existsByEmail("anton@gmail.com");

        assertTrue(result);
    }

    @Test
    void updateProfile_ok() {
        User user = new UserTestBuilder()
                .withId(1L)
                .withUsername("anton")
                .withVisibleName("Old Name")
                .withBio("old bio")
                .withProfileImage("old.png")
                .withPrivacy(PrivacyType.PRIVATE)
                .build();

        UserUpdateRequest request = new UserUpdateRequest(
                "New Name",
                "new bio",
                "new.png",
                PrivacyType.PUBLIC
        );

        List<BookCardResponse> books = List.of();
        List<PostResponse> posts = List.of();

        UserProfileResponse response = new UserProfileResponse(
                1L,
                "anton",
                "New Name",
                "new bio",
                "new.png",
                PrivacyType.PUBLIC,
                user.getCreatedAt(),
                user.getRole(),
                books,
                posts
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(bookRepository.findByAuthor(user)).thenReturn(List.of());
        when(postRepository.findByAuthorOrderByCreatedAtDesc(user)).thenReturn(List.of());
        when(userMapper.toProfileResponse(user, books, posts)).thenReturn(response);

        UserProfileResponse result = userService.updateProfile(1L, request);

        assertEquals("New Name", result.visibleName());
        assertEquals("new bio", result.bio());
        assertEquals("new.png", result.profileImage());
        assertEquals(PrivacyType.PUBLIC, result.privacy());

        verify(userRepository).save(user);
        verify(userMapper).toProfileResponse(user, books, posts);
    }

    @Test
    void updateProfile_visibleNameBlank() {
        User user = new UserTestBuilder()
                .withId(1L)
                .withVisibleName("Old Name")
                .build();

        UserUpdateRequest request = new UserUpdateRequest(
                "   ",
                null,
                null,
                null
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class, () -> userService.updateProfile(1L, request));

        verify(userRepository, never()).save(any(User.class));
        verify(userMapper, never()).toProfileResponse(any(), any(), any());
    }

    @Test
    void updateProfile_userNotFound() {
        UserUpdateRequest request = new UserUpdateRequest(
                "New Name",
                "new bio",
                "new.png",
                PrivacyType.PUBLIC
        );

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateProfile(1L, request));

        verify(userRepository, never()).save(any(User.class));
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

    @Test
    void deleteUser_notFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.deleteUserById(1L));

        verify(userRepository, never()).delete(any(User.class));
    }
}