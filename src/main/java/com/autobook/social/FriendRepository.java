package com.autobook.social;

import org.springframework.data.jpa.repository.JpaRepository;
import com.autobook.user.User;
import java.util.Optional;
import java.util.List;
import com.autobook.entity.FriendshipStatus;

public interface FriendRepository extends JpaRepository<Friend, Long>{

    List<Friend> findByUser(User user);

    List<Friend> findByUserAndStatus(User user, FriendshipStatus status);

    List<Friend> findByFriendAndStatus(User friend, FriendshipStatus status);

    Optional<Friend> findByUserAndFriend(User user, User friend);

    Boolean existsByUserAndFriend(User user, User friend);

    Long countByUserAndStatus(User user, FriendshipStatus status);

}
