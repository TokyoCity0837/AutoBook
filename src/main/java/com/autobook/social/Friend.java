package com.autobook.social;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import com.autobook.user.User;
import java.time.LocalDateTime;
import com.autobook.entity.FriendshipStatus;

@Entity
@Table(name = "friends")
@Getter
@Setter
public class Friend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "friend_id", nullable = false)
    private User friend;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 10)
    private FriendshipStatus status = FriendshipStatus.PENDING;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}