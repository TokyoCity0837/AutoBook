package com.autobook.social;

import com.autobook.user.User;
import com.autobook.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/friends")
public class FriendController {
    private final FriendService friendService;
    private final UserService userService;
    
    public FriendController(FriendService friendService, UserService userService) {
        this.friendService = friendService;
        this.userService = userService;
    }
    
    @PostMapping("/request")
    public ResponseEntity<Friend> sendFriendRequest(
            @RequestParam Long userId,
            @RequestParam Long friendId) {
        User user = userService.getUserById(userId);
        User friend = userService.getUserById(friendId);
        Friend request = friendService.sendFriendRequest(user, friend);
        return ResponseEntity.ok(request);
    }
    
    @PutMapping("/request/{requestId}/accept")
    public ResponseEntity<Friend> acceptFriendRequest(
            @PathVariable Long requestId,
            @RequestParam Long userId) {
        Friend friendship = friendService.acceptFriendRequest(requestId);
        return ResponseEntity.ok(friendship);
    }
    
    @PutMapping("/request/{requestId}/reject")
    public ResponseEntity<Void> rejectFriendRequest(
            @PathVariable Long requestId,
            @RequestParam Long userId) {
        friendService.rejectFriendRequest(requestId);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/request/{requestId}")
    public ResponseEntity<Void> cancelFriendRequest(
            @PathVariable Long requestId,
            @RequestParam Long userId) {
        User user = userService.getUserById(userId);
        friendService.cancelFriendRequest(requestId, user);
        return ResponseEntity.noContent().build();
    }
    
    @DeleteMapping
    public ResponseEntity<Void> removeFriend(
            @RequestParam Long userId,
            @RequestParam Long friendId) {
        User user = userService.getUserById(userId);
        User friend = userService.getUserById(friendId);
        friendService.removeFriend(user, friend);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/requests/pending")
    public ResponseEntity<List<Friend>> getPendingRequests(@RequestParam Long userId) {
        User user = userService.getUserById(userId);
        List<Friend> requests = friendService.getFriendRequests(user);
        return ResponseEntity.ok(requests);
    }
    
    @GetMapping("/requests/sent")
    public ResponseEntity<List<Friend>> getSentRequests(@RequestParam Long userId) {
        User user = userService.getUserById(userId);
        List<Friend> requests = friendService.getSentFriendRequests(user);
        return ResponseEntity.ok(requests);
    }
    
    @GetMapping
    public ResponseEntity<List<Friend>> getFriends(@RequestParam Long userId) {
        User user = userService.getUserById(userId);
        List<Friend> friends = friendService.getFriends(user);
        return ResponseEntity.ok(friends);
    }
    
    @GetMapping("/check")
    public ResponseEntity<Boolean> areFriends(
            @RequestParam Long userId1,
            @RequestParam Long userId2) {
        User user1 = userService.getUserById(userId1);
        User user2 = userService.getUserById(userId2);
        Boolean areFriends = friendService.areFriends(user1, user2);
        return ResponseEntity.ok(areFriends);
    }
    
    @GetMapping("/count")
    public ResponseEntity<Long> getFriendCount(@RequestParam Long userId) {
        User user = userService.getUserById(userId);
        Long count = friendService.getFriendCount(user);
        return ResponseEntity.ok(count);
    }
}