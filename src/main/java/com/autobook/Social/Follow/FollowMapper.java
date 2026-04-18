package com.autobook.Social.Follow;

import com.autobook.Social.Follow.DTO.Response.FollowResponse;
import com.autobook.Social.User.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FollowMapper {

    private final UserMapper userMapper;

    public FollowResponse toResponse(Follow follow) {
        return new FollowResponse(
                follow.getId(),
                userMapper.toCardResponse(follow.getFollower()),
                userMapper.toCardResponse(follow.getFollowing()),
                follow.getStatus(),
                follow.getCreatedAt()
        );
    }
}