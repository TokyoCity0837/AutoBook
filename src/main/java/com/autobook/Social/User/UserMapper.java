package com.autobook.Social.User;

import com.autobook.Library.Book.DTO.Response.BookCardResponse;
import com.autobook.Social.Post.DTO.Response.PostResponse;
import com.autobook.Social.User.DTO.Response.UserCardResponse;
import com.autobook.Social.User.DTO.Response.UserPostDetailsResponse;
import com.autobook.Social.User.DTO.Response.UserProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserMapper {

    public UserProfileResponse toProfileResponse(
            User user,
            List<BookCardResponse> books,
            List<PostResponse> posts,
            long followers,
            long friends
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
                posts
        );
    }

    public UserCardResponse toCardResponse(User user) {
        return new UserCardResponse(
                user.getId(),
                user.getVisibleName(),
                user.getUsername(),
                user.getProfileImage(),
                user.getRole()
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