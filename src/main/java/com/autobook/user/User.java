package com.autobook.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import com.autobook.entity.PrivacyType;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", unique = true, nullable = false, length = 50)
    private String username;

    @Column(name = "email", unique = true, nullable = false, length = 50)
    private String email;

    @Column(name = "password", nullable = false, length = 75)
    private String password;

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @Column(name = "profile_image")
    private String profileImage;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "privacy", length = 10)
    private PrivacyType privacy = PrivacyType.PUBLIC;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
