package com.autobook.Library.Edit;

import com.autobook.Library.Book.Book;
import com.autobook.Social.User.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import com.autobook.Enum.EditStatus;

@Entity
@Table(name = "edit_requests")
@Getter
@Setter
public class Edit{
    
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
    private EditStatus status = EditStatus.PENDING;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
