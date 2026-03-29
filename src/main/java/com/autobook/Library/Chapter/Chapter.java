package com.autobook.Library.Chapter;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.autobook.Library.Book.Book;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "chapters")
@Getter
@Setter
public class Chapter{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;
    
    @Column(name = "title", nullable = false, length = 255)
    private String title;
    
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;
       
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

