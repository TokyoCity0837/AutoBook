package com.autobook.Library.SavedItem;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.autobook.Social.User.User;
import com.autobook.Library.Book.Book;
import com.autobook.Library.Chapter.Chapter;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "saved_items")
@Getter
@Setter
public class SavedItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", nullable = false)
    private ItemType itemType;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = true)
    private Book book;

    @ManyToOne
    @JoinColumn(name = "chapter_id", nullable = true)
    private Chapter chapter;

    @CreationTimestamp
    @Column(name = "saved_at", updatable = false)
    private LocalDateTime savedAt;

    public enum ItemType {
        BOOK, CHAPTER
    }
}
