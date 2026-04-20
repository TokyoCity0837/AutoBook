package com.autobook.Social.Follow;

import com.autobook.Social.Follow.DTO.Response.FollowResponse;
import com.autobook.Social.User.User;
import com.autobook.Social.User.UserRepository;
import com.autobook.Exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/follows")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;
    private final UserRepository userRepository;

    private User getAuthenticatedUser(Principal principal) {
        if (principal == null) throw new UserNotFoundException("Principal is null");
        return userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    @PostMapping("/request/{followingId}")
    @ResponseStatus(HttpStatus.CREATED)
    public FollowResponse sendFollowRequest(@PathVariable Long followingId, Principal principal) {
        User follower = getAuthenticatedUser(principal);
        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return followService.sendFollowRequest(follower, following);
    }

    @PostMapping("/direct/{followingId}")
    @ResponseStatus(HttpStatus.CREATED)
    public FollowResponse directFollow(@PathVariable Long followingId, Principal principal) {
        User follower = getAuthenticatedUser(principal);
        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return followService.directFollow(follower, following);
    }


    @GetMapping("/friends/me")
    public List<FollowResponse> getMyFriends(Principal principal) {
        return followService.getFriends(getAuthenticatedUser(principal));
    }

    @GetMapping("/pending")
    public List<FollowResponse> getPendingRequests(Principal principal) {
        User user = getAuthenticatedUser(principal);
        return followService.getPendingRequests(user);
    }

    @GetMapping("/followers")
    public List<FollowResponse> getFollowers(Principal principal) {
        User user = getAuthenticatedUser(principal);
        return followService.getFollowers(user);
    }

    @GetMapping("/status/{userId}")
    public boolean getFollowStatus(@PathVariable Long userId, Principal principal) {
        User me = getAuthenticatedUser(principal);
        User target = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return followService.isFollowing(me, target);
    }

    @GetMapping("/following")
    public List<FollowResponse> getFollowing(Principal principal) {
        User user = getAuthenticatedUser(principal);
        return followService.getFollowing(user);
    }

    @PutMapping("/{id}/accept")
    public FollowResponse acceptFollowRequest(@PathVariable Long id) {
        return followService.acceptFollowRequest(id);
    }

    @DeleteMapping("/{id}/reject")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void rejectFollowRequest(@PathVariable Long id) {
        followService.rejectFollowRequest(id);
    }

    @DeleteMapping("/remove/{secondUserId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeConnection(@PathVariable Long secondUserId, Principal principal) {
        User firstUser = getAuthenticatedUser(principal);
        User secondUser = userRepository.findById(secondUserId).orElseThrow(() -> new UserNotFoundException("User not found"));
        followService.removeConnection(firstUser, secondUser);
    }
}
