package com.autobook.Social.User;

import com.autobook.Enum.PrivacyType;
import com.autobook.Enum.UserRole;
import com.autobook.Exception.EmailAlreadyInUseException;
import com.autobook.Exception.UserNotFoundException;
import com.autobook.Exception.UsernameAlreadyExistsException;
import com.autobook.Factory.UserFactory;
import com.autobook.Library.Book.BookRepository;
import com.autobook.Library.Book.BookMapper;
import com.autobook.Library.Book.DTO.Response.BookCardResponse;
import com.autobook.Social.Post.PostMapper;
import com.autobook.Social.Post.PostRepository;
import com.autobook.Social.Post.DTO.Response.PostResponse;
import com.autobook.Social.User.DTO.Request.UserRegisterRequest;
import com.autobook.Social.User.DTO.Request.UserUpdateRequest;
import com.autobook.Social.User.DTO.Response.UserCardResponse;
import com.autobook.Social.User.DTO.Response.UserProfileResponse;
import com.autobook.Social.Follow.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserFactory userFactory;
    private final UserMapper userMapper;

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    private final PostRepository postRepository;
    private final PostMapper postMapper;

    private final FollowService followService;

    @Transactional
    public UserProfileResponse createUser(UserRegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyInUseException(request.email());
        }

        if (userRepository.existsByUsername(request.username())) {
            throw new UsernameAlreadyExistsException(request.username());
        }

        String encodedPassword = passwordEncoder.encode(request.password());

        User user = userFactory.create(
                request.username(),
                request.visibleName(),
                request.email(),
                encodedPassword
        );

        User savedUser = userRepository.save(user);
        return buildUserProfileResponse(savedUser);
    }

    public UserProfileResponse getUserProfileById(Long userId) {
        User user = getUserEntityById(userId);
        return buildUserProfileResponse(user);
    }

    public UserProfileResponse getUserProfileByUsername(String username) {
        User user = getUserEntityByUsername(username);
        return buildUserProfileResponse(user);
    }

    public List<UserCardResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toCardResponse)
                .toList();
    }

    public List<UserCardResponse> getUsersByRole(UserRole role) {
        return userRepository.findByRole(role)
                .stream()
                .map(userMapper::toCardResponse)
                .toList();
    }

    public List<UserCardResponse> searchUsersByUsername(String username) {
        return userRepository.findByUsernameContainingIgnoreCase(username)
                .stream()
                .map(userMapper::toCardResponse)
                .toList();
    }

    public List<UserCardResponse> getUsersByPrivacy(PrivacyType privacy) {
        return userRepository.findByPrivacy(privacy)
                .stream()
                .map(userMapper::toCardResponse)
                .toList();
    }

    public Long countUsersByPrivacy(PrivacyType privacy) {
        return userRepository.countByPrivacy(privacy);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional
    public UserProfileResponse updateProfile(Long userId, UserUpdateRequest request) {
        User user = getUserEntityById(userId);

        if (request.visibleName() != null) {
            if (request.visibleName().isBlank()) {
                throw new IllegalArgumentException("Visible name cannot be blank");
            }
            user.setVisibleName(request.visibleName());
        }

        if (request.bio() != null) {
            user.setBio(request.bio());
        }

        if (request.profileImage() != null) {
            user.setProfileImage(request.profileImage());
        }

        if (request.privacyType() != null) {
            user.setPrivacy(request.privacyType());
        }

        User savedUser = userRepository.save(user);
        return buildUserProfileResponse(savedUser);
    }

    @Transactional
    public void deleteUserById(Long userId) {
        User user = getUserEntityById(userId);
        userRepository.delete(user);
    }

    private UserProfileResponse buildUserProfileResponse(User user) {
        List<BookCardResponse> books = bookRepository.findByAuthor(user)
                .stream()
                .map(bookMapper::toCardResponse)
                .toList();

        List<PostResponse> posts = postRepository.findByAuthorOrderByCreatedAtDesc(user)
                .stream()
                .map(postMapper::toResponse)
                .toList();

        long followerCount = followService.countFollowers(user);
        // Friends = mutual follows
        long friendCount = followService.getFriends(user).size();

        return userMapper.toProfileResponse(user, books, posts, followerCount, friendCount);
    }

    private User getUserEntityById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with provided ID was not found"));
    }

    private User getUserEntityByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User with provided username was not found"));
    }

    private User getUserEntityByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with provided email was not found"));
    }
}