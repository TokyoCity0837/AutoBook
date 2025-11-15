package com.autobook.social;

import com.autobook.book.Book;
import com.autobook.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import com.autobook.entity.EditRequestStatus;

@Entity
@Table(name = "edit_requests")
@Getter
@Setter
public class EditRequest{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;
    
    @ManyToOne
    @JoinColumn(name = "from_user_id", nullable = false)
    private User fromUser;
    
    @Column(name = "message", columnDefinition = "TEXT")
    private String message;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 10)
    private EditRequestStatus status = EditRequestStatus.PENDING;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}