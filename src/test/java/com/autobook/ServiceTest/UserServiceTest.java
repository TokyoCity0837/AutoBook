package com.autobook.ServiceTest;

import com.autobook.Enum.PrivacyType;
import com.autobook.Enum.UserRole;
import com.autobook.Exception.EmailAlreadyInUseException;
import com.autobook.Exception.UserNotFoundException;
import com.autobook.Exception.UsernameAlreadyExistsException;
import com.autobook.Factory.UserFactory;
import com.autobook.Generic.AsyncActivityLogger;
import com.autobook.Library.Book.BookMapper;
import com.autobook.Library.Book.BookRepository;
import com.autobook.Library.Book.DTO.Response.BookCardResponse;
import com.autobook.Social.Post.PostMapper;
import com.autobook.Social.Post.PostRepository;
import com.autobook.Social.Post.PostReposts.PostRepostRepository;
import com.autobook.Social.User.DTO.Request.UserRegisterRequest;
import com.autobook.Social.User.DTO.Request.UserUpdateRequest;
import com.autobook.Social.User.DTO.Response.ProfilePostItemResponse;
import com.autobook.Social.User.DTO.Response.UserCardResponse;
import com.autobook.Social.User.DTO.Response.UserProfileResponse;
import com.autobook.Social.User.User;
import com.autobook.Social.User.UserMapper;
import com.autobook.Social.User.UserRepository;
import com.autobook.Social.User.UserService;
import com.autobook.Social.Follow.FollowService;
import com.autobook.util.UserTestBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

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

        @Mock
        private FollowService followService;

        @Mock
        private PostRepostRepository postRepostRepository;

        @Mock
        private com.autobook.Social.Post.PostLikes.PostLikeRepository postLikeRepository;

        @Mock
        private AsyncActivityLogger asyncActivityLogger;

        @InjectMocks
        private UserService userService;

        private UserCardResponse cardResponse(long id, String visibleName, String username) {
                return new UserCardResponse(id, visibleName, username, "user.png", UserRole.USER, false);
        }

        private UserProfileResponse profileResponse(User user,
                        List<BookCardResponse> books,
                        List<ProfilePostItemResponse> posts) {
                return new UserProfileResponse(
                                user.getId(),
                                user.getUsername(),
                                user.getVisibleName(),
                                user.getBio(),
                                user.getProfileImage(),
                                user.getPrivacy(),
                                user.getCreatedAt(),
                                user.getRole(),
                                0L,
                                0L,
                                books,
                                posts,
                                false,
                                false);
        }

        @Test
        void createUser_ok() {
                UserRegisterRequest request = new UserRegisterRequest(
                                "anton", "Anton", "anton@gmail.com", "rawPassword");

                User createdUser = new UserTestBuilder()
                                .withId(1L)
                                .withUsername("anton")
                                .withVisibleName("Anton")
                                .withEmail("anton@gmail.com")
                                .withPassword("encodedPassword")
                                .build();

                List<BookCardResponse> books = List.of();
                List<ProfilePostItemResponse> posts = List.of();
                UserProfileResponse response = profileResponse(createdUser, books, posts);

                when(userRepository.existsByEmail("anton@gmail.com")).thenReturn(false);
                when(userRepository.existsByUsername("anton")).thenReturn(false);
                when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPassword");
                when(userFactory.create("anton", "Anton", "anton@gmail.com",
                                "encodedPassword"))
                                .thenReturn(createdUser);
                when(userRepository.save(createdUser)).thenReturn(createdUser);
                when(bookRepository.findByAuthor(createdUser)).thenReturn(List.of());
                when(postRepository.findByAuthorOrderByCreatedAtDesc(createdUser)).thenReturn(List.of());
                when(userMapper.toProfileResponse(
                                eq(createdUser), eq(books), eq(posts),
                                anyLong(), anyLong(), anyBoolean(), anyBoolean())).thenReturn(response);

                UserProfileResponse result = userService.createUser(request);

                assertNotNull(result);
                assertEquals("anton", result.username());
                assertEquals("Anton", result.visibleName());
                assertTrue(result.books().isEmpty());
                assertTrue(result.posts().isEmpty());

                verify(userRepository).save(createdUser);
                verify(bookRepository).findByAuthor(createdUser);
                verify(postRepository).findByAuthorOrderByCreatedAtDesc(createdUser);
                verify(userMapper).toProfileResponse(
                                eq(createdUser), eq(books), eq(posts),
                                anyLong(), anyLong(), anyBoolean(), anyBoolean());
        }

        @Test
        void createUser_emailExists() {
                UserRegisterRequest request = new UserRegisterRequest(
                                "anton", "Anton", "anton@gmail.com", "rawPassword");

                when(userRepository.existsByEmail("anton@gmail.com")).thenReturn(true);

                assertThrows(EmailAlreadyInUseException.class, () -> userService.createUser(request));

                verify(userRepository, never()).existsByUsername(anyString());
                verify(userRepository, never()).save(any(User.class));
                verify(userMapper, never()).toProfileResponse(
                                any(), any(), any(), anyLong(), anyLong(), anyBoolean(), anyBoolean());
        }

        @Test
        void createUser_usernameExists() {
                UserRegisterRequest request = new UserRegisterRequest(
                                "anton", "Anton", "anton@gmail.com", "rawPassword");

                when(userRepository.existsByEmail("anton@gmail.com")).thenReturn(false);
                when(userRepository.existsByUsername("anton")).thenReturn(true);

                assertThrows(UsernameAlreadyExistsException.class, () -> userService.createUser(request));

                verify(userRepository, never()).save(any(User.class));
                verify(userMapper, never()).toProfileResponse(
                                any(), any(), any(), anyLong(), anyLong(), anyBoolean(), anyBoolean());
        }

        @Test
        void getUserProfileById_ok() {
                User user = new UserTestBuilder()
                                .withId(1L)
                                .withUsername("anton")
                                .withVisibleName("Anton")
                                .build();

                UserCardResponse card = cardResponse(1L, "Anton", "anton");
                BookCardResponse bookResponse = new BookCardResponse(10L, "My Book", "cover.png", card,
                                PrivacyType.PUBLIC);

                ProfilePostItemResponse postItem = mock(ProfilePostItemResponse.class);

                List<BookCardResponse> books = List.of(bookResponse);
                List<ProfilePostItemResponse> posts = List.of(postItem);
                UserProfileResponse response = profileResponse(user, books, posts);

                when(userRepository.findById(1L)).thenReturn(Optional.of(user));
                when(bookRepository.findByAuthor(user))
                                .thenReturn(List.of(mock(com.autobook.Library.Book.Book.class)));
                when(postRepository.findByAuthorOrderByCreatedAtDesc(user))
                                .thenReturn(List.of(mock(com.autobook.Social.Post.Post.class)));
                when(bookMapper.toCardResponse(any())).thenReturn(bookResponse);
                when(postMapper.toResponse(any(), anyBoolean(), anyBoolean()))
                                .thenReturn(mock(com.autobook.Social.Post.DTO.Response.PostResponse.class));
                when(userMapper.toProfileResponse(
                                eq(user), any(), any(),
                                anyLong(), anyLong(), anyBoolean(), anyBoolean())).thenReturn(response);

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
                List<ProfilePostItemResponse> posts = List.of();
                UserProfileResponse response = profileResponse(user, books, posts);

                when(userRepository.findByUsername("anton")).thenReturn(Optional.of(user));
                when(bookRepository.findByAuthor(user)).thenReturn(List.of());
                when(postRepository.findByAuthorOrderByCreatedAtDesc(user)).thenReturn(List.of());
                when(userMapper.toProfileResponse(
                                eq(user), eq(books), eq(posts),
                                anyLong(), anyLong(), anyBoolean(), anyBoolean())).thenReturn(response);

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
                                .withId(2L).withUsername("anton")
                                .withVisibleName("Anton").withProfileImage("anton.png")
                                .withRole(UserRole.USER).build();

                User user2 = new UserTestBuilder()
                                .withId(3L).withUsername("admin")
                                .withVisibleName("Admin").withProfileImage("admin.png")
                                .withRole(UserRole.ADMIN).build();

                UserCardResponse response1 = new UserCardResponse(2L, "Anton", "anton", "anton.png", UserRole.USER,
                                false);
                UserCardResponse response2 = new UserCardResponse(3L, "Admin", "admin", "admin.png", UserRole.ADMIN,
                                false);

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
                                .withId(1L).withUsername("admin")
                                .withRole(UserRole.ADMIN).withVisibleName("Admin")
                                .withProfileImage("admin.png").build();

                UserCardResponse response = new UserCardResponse(1L, "Admin", "admin", "admin.png", UserRole.ADMIN,
                                false);

                when(userRepository.findByRole(UserRole.ADMIN)).thenReturn(List.of(user));
                when(userMapper.toCardResponse(user)).thenReturn(response);

                List<UserCardResponse> result = userService.getUsersByRole(UserRole.ADMIN);

                assertEquals(1, result.size());
                assertEquals(UserRole.ADMIN, result.get(0).userRole());
        }

        @Test
        void searchUsersByUsername_ok() {
                User user = new UserTestBuilder()
                                .withId(1L).withUsername("anton")
                                .withVisibleName("Anton").withProfileImage("anton.png").build();

                UserCardResponse response = new UserCardResponse(1L, "Anton", "anton", "anton.png", UserRole.USER,
                                false);

                when(userRepository.findByUsernameContainingIgnoreCase("ant")).thenReturn(List.of(user));
                when(userMapper.toCardResponse(user)).thenReturn(response);

                List<UserCardResponse> result = userService.searchUsersByUsername("ant");

                assertEquals(1, result.size());
                assertEquals("Anton", result.get(0).visibleName());
        }

        @Test
        void getUsersByPrivacy_ok() {
                User user = new UserTestBuilder()
                                .withId(1L).withUsername("anton")
                                .withPrivacy(PrivacyType.PUBLIC)
                                .withVisibleName("Anton").withProfileImage("anton.png").build();

                UserCardResponse response = new UserCardResponse(1L, "Anton", "anton", "anton.png", UserRole.USER,
                                false);

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
                assertTrue(userService.existsByUsername("anton"));
        }

        @Test
        void existsByEmail_ok() {
                when(userRepository.existsByEmail("anton@gmail.com")).thenReturn(true);
                assertTrue(userService.existsByEmail("anton@gmail.com"));
        }

        @Test
        void updateProfile_ok() {
                User user = new UserTestBuilder()
                                .withId(1L).withUsername("anton")
                                .withVisibleName("Old Name").withBio("old bio")
                                .withProfileImage("old.png").withPrivacy(PrivacyType.PRIVATE).build();

                UserUpdateRequest request = new UserUpdateRequest(
                                "New Name", "new bio", "new.png", PrivacyType.PUBLIC, "anton");

                List<BookCardResponse> books = List.of();
                List<ProfilePostItemResponse> posts = List.of();

                UserProfileResponse response = new UserProfileResponse(
                                1L, "anton", "New Name", "new bio", "new.png",
                                PrivacyType.PUBLIC, user.getCreatedAt(), user.getRole(),
                                0L, 0L, books, posts, false, false);

                when(userRepository.findById(1L)).thenReturn(Optional.of(user));
                when(userRepository.save(user)).thenReturn(user);
                when(bookRepository.findByAuthor(user)).thenReturn(List.of());
                when(postRepository.findByAuthorOrderByCreatedAtDesc(user)).thenReturn(List.of());
                when(userMapper.toProfileResponse(
                                eq(user), eq(books), eq(posts),
                                anyLong(), anyLong(), anyBoolean(), anyBoolean())).thenReturn(response);

                UserProfileResponse result = userService.updateProfile(1L, request);

                assertEquals("New Name", result.visibleName());
                assertEquals("new bio", result.bio());
                assertEquals("new.png", result.profileImage());
                assertEquals(PrivacyType.PUBLIC, result.privacy());

                verify(userRepository).save(user);
                verify(userMapper).toProfileResponse(
                                eq(user), eq(books), eq(posts),
                                anyLong(), anyLong(), anyBoolean(), anyBoolean());
        }

        @Test
        void updateProfile_visibleNameBlank() {
                User user = new UserTestBuilder()
                                .withId(1L).withVisibleName("Old Name").build();

                UserUpdateRequest request = new UserUpdateRequest("   ", null, null, null, null);

                when(userRepository.findById(1L)).thenReturn(Optional.of(user));

                assertThrows(IllegalArgumentException.class, () -> userService.updateProfile(1L, request));

                verify(userRepository, never()).save(any(User.class));
                verify(userMapper, never()).toProfileResponse(
                                any(), any(), any(), anyLong(), anyLong(), anyBoolean(), anyBoolean());
        }

        @Test
        void updateProfile_userNotFound() {
                UserUpdateRequest request = new UserUpdateRequest(
                                "New Name", "new bio", "new.png", PrivacyType.PUBLIC, "anton");

                when(userRepository.findById(1L)).thenReturn(Optional.empty());

                assertThrows(UserNotFoundException.class, () -> userService.updateProfile(1L, request));

                verify(userRepository, never()).save(any(User.class));
        }

        @Test
        void updateProfile_usernameBlank() {
                User user = new UserTestBuilder().withId(1L).withUsername("old").build();
                UserUpdateRequest req = new UserUpdateRequest("name", "bio", "img", PrivacyType.PUBLIC, "  ");
                when(userRepository.findById(1L)).thenReturn(Optional.of(user));

                assertThrows(IllegalArgumentException.class, () -> userService.updateProfile(1L, req));
                verify(userRepository, never()).save(any(User.class));
        }

        @Test
        void updateProfile_usernameAlreadyExists() {
                User user = new UserTestBuilder().withId(1L).withUsername("old").build();
                UserUpdateRequest req = new UserUpdateRequest("name", "bio", "img", PrivacyType.PUBLIC, "new_username");
                when(userRepository.findById(1L)).thenReturn(Optional.of(user));
                when(userRepository.existsByUsername("new_username")).thenReturn(true);

                assertThrows(UsernameAlreadyExistsException.class, () -> userService.updateProfile(1L, req));
                verify(userRepository, never()).save(any(User.class));
        }

        @Test
        void deleteUser_ok() {
                User user = new UserTestBuilder().withId(1L).build();

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

        @Test
        void getAllUsers_ReturnsList() {
                User user = new UserTestBuilder().withId(1L).build();
                UserCardResponse card = new UserCardResponse(1L, "Visible", "username", null, UserRole.USER, false);

                when(userRepository.findAll()).thenReturn(List.of(user));
                when(userMapper.toCardResponse(user)).thenReturn(card);

                List<UserCardResponse> res = userService.getAllUsers();
                assertEquals(1, res.size());
                assertEquals("username", res.get(0).username());
        }

        @Test
        void getUsersByRole_ReturnsList() {
                User user = new UserTestBuilder().withId(1L).withRole(UserRole.ADMIN).build();
                UserCardResponse card = new UserCardResponse(1L, "Visible", "admin", null, UserRole.ADMIN, false);

                when(userRepository.findByRole(UserRole.ADMIN)).thenReturn(List.of(user));
                when(userMapper.toCardResponse(user)).thenReturn(card);

                List<UserCardResponse> res = userService.getUsersByRole(UserRole.ADMIN);
                assertEquals(1, res.size());
        }

        @Test
        void searchUsersByUsername_ReturnsList() {
                User user = new UserTestBuilder().withId(1L).build();
                when(userRepository.findByUsernameContainingIgnoreCase("t")).thenReturn(List.of(user));
                when(userMapper.toCardResponse(user)).thenReturn(null);

                assertEquals(1, userService.searchUsersByUsername("t").size());
        }

        @Test
        void getUsersByPrivacy_ReturnsList() {
                User user = new UserTestBuilder().withId(1L).build();
                when(userRepository.findByPrivacy(PrivacyType.PUBLIC)).thenReturn(List.of(user));
                when(userMapper.toCardResponse(user)).thenReturn(null);

                assertEquals(1, userService.getUsersByPrivacy(PrivacyType.PUBLIC).size());
        }

        @Test
        void countUsersByPrivacy_ReturnsCount() {
                when(userRepository.countByPrivacy(PrivacyType.PRIVATE)).thenReturn(10L);
                assertEquals(10L, userService.countUsersByPrivacy(PrivacyType.PRIVATE));
        }

        @Test
        void existsByEmail_ReturnsTrue() {
                when(userRepository.existsByEmail("test@test.com")).thenReturn(true);
                assertTrue(userService.existsByEmail("test@test.com"));
        }

        @Test
        void existsByUsername_ReturnsTrue() {
                when(userRepository.existsByUsername("test")).thenReturn(true);
                assertTrue(userService.existsByUsername("test"));
        }

        // Update Profile

        @Test
        void updateProfile_Ok() {
                User user = new UserTestBuilder().withId(1L).withUsername("old").build();
                when(userRepository.findById(1L)).thenReturn(Optional.of(user));

                UserUpdateRequest req = new UserUpdateRequest("New Name", "Bio", "img", PrivacyType.PRIVATE,
                                "new_username");

                when(userRepository.existsByUsername("new_username")).thenReturn(false);
                when(userRepository.save(user)).thenReturn(user);

                // Security stub
                SecurityContext ctx = mock(SecurityContext.class);
                Authentication auth = mock(Authentication.class);
                when(ctx.getAuthentication()).thenReturn(auth);
                when(auth.isAuthenticated()).thenReturn(false);
                SecurityContextHolder.setContext(ctx);

                when(userMapper.toProfileResponse(any(), any(), any(), anyLong(), anyLong(), anyBoolean(),
                                anyBoolean()))
                                .thenReturn(
                                                new UserProfileResponse(1L, "new_username", "New Name", "Bio", "img",
                                                                PrivacyType.PRIVATE, null,
                                                                UserRole.USER, 0, 0, null, null, false, false));

                UserProfileResponse res = userService.updateProfile(1L, req);
                assertEquals("new_username", res.username());
        }

        @Test
        void deleteUserById_Ok() {
                User user = new UserTestBuilder().withId(1L).build();
                when(userRepository.findById(1L)).thenReturn(Optional.of(user));

                userService.deleteUserById(1L);

                verify(userRepository).delete(user);
        }

        @Test
        void getUserProfileById_withTimeline() {

                User createdUser = new UserTestBuilder().withId(1L).withUsername("anton").build();
                SecurityContext context = mock(SecurityContext.class);
                Authentication auth = mock(Authentication.class);
                when(context.getAuthentication()).thenReturn(auth);
                when(auth.isAuthenticated()).thenReturn(true);
                when(auth.getName()).thenReturn("anton");
                SecurityContextHolder.setContext(context);

                com.autobook.Social.Post.Post mockPost = new com.autobook.Social.Post.Post();
                mockPost.setId(10L);
                mockPost.setCreatedAt(java.time.LocalDateTime.now());
                when(postRepository.findByAuthorOrderByCreatedAtDesc(createdUser)).thenReturn(List.of(mockPost));

                com.autobook.Social.Post.Post repOriginal = new com.autobook.Social.Post.Post();
                repOriginal.setId(11L);
                repOriginal.setCreatedAt(java.time.LocalDateTime.now());

                com.autobook.Social.Post.PostReposts.PostRepost mockRepost = new com.autobook.Social.Post.PostReposts.PostRepost(
                                createdUser, repOriginal);
                org.springframework.test.util.ReflectionTestUtils.setField(mockRepost, "createdAt", java.time.LocalDateTime.now());

                when(postRepostRepository.findByUserOrderByCreatedAtDesc(createdUser)).thenReturn(List.of(mockRepost));

                when(userRepository.findById(1L)).thenReturn(Optional.of(createdUser));
                when(userRepository.findByUsername("anton")).thenReturn(Optional.of(createdUser));

                List<BookCardResponse> books = List.of();
                List<ProfilePostItemResponse> posts = List.of();
                UserProfileResponse response = profileResponse(createdUser, books, posts);
                when(userMapper.toProfileResponse(
                                eq(createdUser), any(), any(),
                                anyLong(), anyLong(), anyBoolean(), anyBoolean())).thenReturn(response);

                UserProfileResponse result = userService.getUserProfileById(1L);

                assertNotNull(result);
        }
}