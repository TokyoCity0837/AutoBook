package com.autobook.Social.Follow;

import com.autobook.Social.Follow.DTO.Response.FollowResponse;
import com.autobook.Social.User.DTO.Response.UserCardResponse;
import com.autobook.Social.User.User;
import com.autobook.Social.User.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FollowMapper {

    private UserCardResponse toCard(User user) {
        return new UserCardResponse(
                user.getId(),
                user.getVisibleName(),
                user.getUsername(),
                user.getProfileImage(),
                user.getRole(),
                false
        );
    }

    public FollowResponse toResponse(Follow follow) {
        return new FollowResponse(
                follow.getId(),
                toCard(follow.getFollower()),
                toCard(follow.getFollowing()),
                follow.getStatus(),
                follow.getCreatedAt()
        );
    }

}