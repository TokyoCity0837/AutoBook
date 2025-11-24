package com.autobook.book;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "vocabulary")
@Getter
@Setter
public class Vocabulary{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;
    
    @Column(name = "word", nullable = false, length = 15)
    private String word;
    
    @Column(name = "meaning", columnDefinition = "TEXT")
    private String meaning;
        
    @Column(name = "usage_example", columnDefinition = "TEXT")
    private String usageExample;
    
}
