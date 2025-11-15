package com.autobook.book;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.autobook.user.User;
import com.autobook.entity.PrivacyType;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "books")
@Getter
@Setter
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User author;
    
    @Column(name = "title", nullable = false, length = 255)
    private String title;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "genre", length = 50)
    private String genre;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "privacy", length = 10)
    private PrivacyType privacy = PrivacyType.PRIVATE;
    
    @Column(name = "is_featured")
    private Boolean isFeatured = false;
    
    @Column(name = "cover_image")
    private String coverImage;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
