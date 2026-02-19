package com.autobook.social;

import com.autobook.user.User;
import com.autobook.entity.FriendshipStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FriendService {
    private final FriendRepository friendRepository;
    
    public FriendService(FriendRepository friendRepository) {
        this.friendRepository = friendRepository;
    }
    
    public Friend sendFriendRequest(User user, User friendUser) {
        
        if (friendRepository.existsByUserAndFriend(user, friendUser)) {
            throw new RuntimeException("Friends request has been already sent");
        }
        
        if (friendRepository.existsByUserAndFriend(friendUser, user)) {
            throw new RuntimeException("This user already sent you a friend request");
        }
        
        Friend friendRequest = new Friend();
        friendRequest.setUser(user);
        friendRequest.setFriend(friendUser);
        friendRequest.setStatus(FriendshipStatus.PENDING);
        
        return friendRepository.save(friendRequest);
    }
    
    public Friend acceptFriendRequest(Long requestId) {
        Friend request = findFriendById(requestId);
        
        if (request.getStatus() != FriendshipStatus.PENDING) {
            throw new RuntimeException("This request has already been processed");
        }
        
        request.setStatus(FriendshipStatus.ACCEPTED);
        
        Friend reverseFriend = new Friend();
        reverseFriend.setUser(request.getFriend());
        reverseFriend.setFriend(request.getUser());
        reverseFriend.setStatus(FriendshipStatus.ACCEPTED);
        
        friendRepository.save(reverseFriend);
        return friendRepository.save(request);
    }
    
    public void rejectFriendRequest(Long requestId) {
        Friend request = findFriendById(requestId);
        
        if (request.getStatus() != FriendshipStatus.PENDING) {
            throw new RuntimeException("This request has already been processed");
        }
        
        request.setStatus(FriendshipStatus.DECLINED);
        friendRepository.save(request);
    }
    
    public void cancelFriendRequest(Long requestId, User user) {
        Friend request = findFriendById(requestId);
        
        if (!request.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You can only cancel your own friend requests");
        }
        
        if (request.getStatus() != FriendshipStatus.PENDING) {
            throw new RuntimeException("Cannot cancel a processed request");
        }
        
        friendRepository.delete(request);
    }
    
    public void removeFriend(User user, User friendToRemove) {
        Optional<Friend> friendship1 = friendRepository.findByUserAndFriend(user, friendToRemove);
        friendship1.ifPresent(friendRepository::delete);
        
        Optional<Friend> friendship2 = friendRepository.findByUserAndFriend(friendToRemove, user);
        friendship2.ifPresent(friendRepository::delete);
    }
    
    public List<Friend> getFriendRequests(User user) {
        return friendRepository.findByFriendAndStatus(user, FriendshipStatus.PENDING);
    }
    
    public List<Friend> getSentFriendRequests(User user) {
        return friendRepository.findByUserAndStatus(user, FriendshipStatus.PENDING);
    }
    
    public List<Friend> getFriends(User user) {
        return friendRepository.findByUserAndStatus(user, FriendshipStatus.ACCEPTED);
    }
    
    public boolean areFriends(User user1, User user2) {
        Optional<Friend> friendship = friendRepository.findByUserAndFriend(user1, user2);
        return friendship.isPresent() && 
               friendship.get().getStatus() == FriendshipStatus.ACCEPTED;
    }
    
    public Long getFriendCount(User user) {
        return friendRepository.countByUserAndStatus(user, FriendshipStatus.ACCEPTED);
    }
    
    private Friend findFriendById(Long id) {
        return friendRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Friendship record not found"));
    }
}