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
import com.autobook.Social.Post.PostReposts.PostRepostRepository;
import com.autobook.Social.User.DTO.Request.UserRegisterRequest;
import com.autobook.Social.User.DTO.Request.UserUpdateRequest;
import com.autobook.Social.User.DTO.Response.UserCardResponse;
import com.autobook.Social.User.DTO.Response.UserProfileResponse;
import com.autobook.Social.Follow.FollowService;
import com.autobook.Social.Post.PostLikes.PostLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.autobook.Social.User.DTO.Response.ProfilePostItemResponse;
import java.util.Comparator;
import java.util.stream.Stream;

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
    private final PostLikeRepository postLikeRepository;
    private final PostRepostRepository postRepostRepository;
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

        if (request.username() != null) {
            if (request.username().isBlank()) {
                throw new IllegalArgumentException("Username cannot be blank");
            }
            if (!request.username().equals(user.getUsername()) &&
                    userRepository.existsByUsername(request.username())) {
                throw new UsernameAlreadyExistsException(request.username());
            }
            user.setUsername(request.username());
        }
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
        var auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser");

        User currentUser;
        if (isAuthenticated) {
            currentUser = userRepository.findByUsername(auth.getName()).orElse(null);
        } else {
            currentUser = null;
        }

        boolean isOwner = currentUser != null && currentUser.getId().equals(user.getId());
        boolean isFriend = currentUser != null && !isOwner && followService.areFriends(currentUser, user);
        boolean isPrivate = user.getPrivacy() == PrivacyType.PRIVATE;
        boolean canSeeContent = isOwner || isFriend || !isPrivate;

        List<BookCardResponse> books = canSeeContent
                ? bookRepository.findByAuthor(user)
                .stream()
                .map(bookMapper::toCardResponse)
                .filter(p -> p.privacy() == PrivacyType.PUBLIC)
                .toList()
                : List.of();

        List<ProfilePostItemResponse> posts = canSeeContent
                ? buildProfileTimeline(user, currentUser)
                : List.of();

        long followerCount = followService.countFollowers(user);
        long friendCount = followService.getFriends(user).size();

        return userMapper.toProfileResponse(
                user, books, posts, followerCount, friendCount, isFriend, isPrivate && !canSeeContent
        );
    }

    private List<ProfilePostItemResponse> buildProfileTimeline(User profileOwner, User currentUser) {
        var ownPosts = postRepository.findByAuthorOrderByCreatedAtDesc(profileOwner)
                .stream()
                .map(post -> {
                    boolean liked = currentUser != null &&
                            postLikeRepository.existsByUserIdAndPostId(currentUser.getId(), post.getId());
                    boolean reposted = currentUser != null &&
                            postRepostRepository.existsByIdUserIdAndIdPostId(currentUser.getId(), post.getId());

                    PostResponse postResponse = postMapper.toResponse(post, liked, reposted);

                    return new ProfilePostItemResponse(
                            "POST",
                            postResponse,
                            null,
                            null,
                            post.getCreatedAt()
                    );
                });

        var reposts = postRepostRepository.findByUserOrderByCreatedAtDesc(profileOwner)
                .stream()
                .map(repost -> {
                    var originalPost = repost.getPost();

                    boolean liked = currentUser != null &&
                            postLikeRepository.existsByUserIdAndPostId(currentUser.getId(), originalPost.getId());
                    boolean repostedByMe = currentUser != null &&
                            postRepostRepository.existsByIdUserIdAndIdPostId(currentUser.getId(), originalPost.getId());

                    PostResponse postResponse = postMapper.toResponse(originalPost, liked, repostedByMe);

                    return new ProfilePostItemResponse(
                            "REPOST",
                            postResponse,
                            userMapper.toCardResponse(profileOwner),
                            repost.getCreatedAt(),
                            repost.getCreatedAt()
                    );
                });

        return Stream.concat(ownPosts, reposts)
                .sorted(Comparator.comparing(ProfilePostItemResponse::activityAt).reversed())
                .toList();
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