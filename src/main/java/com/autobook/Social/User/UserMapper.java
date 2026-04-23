package com.autobook.Social.User;

import com.autobook.Library.Book.DTO.Response.BookCardResponse;
import com.autobook.Social.Follow.FollowService;
import com.autobook.Social.Post.DTO.Response.PostResponse;
import com.autobook.Social.User.DTO.Response.ProfilePostItemResponse;
import com.autobook.Social.User.DTO.Response.UserCardResponse;
import com.autobook.Social.User.DTO.Response.UserPostDetailsResponse;
import com.autobook.Social.User.DTO.Response.UserProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final FollowService followService;
    private final UserRepository userRepository;

    public UserProfileResponse toProfileResponse(
            User user,
            List<BookCardResponse> books,
            List<ProfilePostItemResponse> posts,
            long followers,
            long friends,
            boolean isFriend,
            boolean isPrivate
    ) {
        return new UserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getVisibleName(),
                user.getBio(),
                user.getProfileImage(),
                user.getPrivacy(),
                user.getCreatedAt(),
                user.getRole(),
                followers,
                friends,
                books,
                posts,
                isFriend,
                isPrivate
        );
    }

    public UserCardResponse toCardResponse(User user) {
        boolean isFriend = false;

        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            User currentUser = userRepository.findByUsername(auth.getName()).orElse(null);
            if (currentUser != null) {
                isFriend = followService.areFriends(currentUser, user);
            }
        }

        return new UserCardResponse(
                user.getId(),
                user.getVisibleName(),
                user.getUsername(),
                user.getProfileImage(),
                user.getRole(),
                isFriend
        );
    }


    public UserPostDetailsResponse toPostDetailsResponse(User user) {
        return new UserPostDetailsResponse(
                user.getId(),
                user.getVisibleName(),
                user.getProfileImage(),
                user.getBio(),
                user.getRole()
        );
    }
}