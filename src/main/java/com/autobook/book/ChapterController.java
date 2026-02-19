package com.autobook.book;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books/{bookId}/chapters")
public class ChapterController {
    private final ChapterService chapterService;
    private final BookService bookService;
    
    public ChapterController(ChapterService chapterService, BookService bookService) {
        this.chapterService = chapterService;
        this.bookService = bookService;
    }
    
    @PostMapping
    public ResponseEntity<Chapter> createChapter(
            @PathVariable Long bookId,
            @RequestBody ChapterRequest request) {
        Book book = bookService.getBookById(bookId);
        Chapter chapter = chapterService.createChapter(
            book,
            request.getTitle(),
            request.getContent()
        );
        return ResponseEntity.ok(chapter);
    }
    
    @GetMapping
    public ResponseEntity<List<Chapter>> getChaptersByBook(@PathVariable Long bookId) {
        Book book = bookService.getBookById(bookId);
        List<Chapter> chapters = chapterService.getChaptersByBook(book);
        return ResponseEntity.ok(chapters);
    }
    
    @GetMapping("/{chapterId}")
    public ResponseEntity<Chapter> getChapterById(@PathVariable Long chapterId) {
        Chapter chapter = chapterService.getChapterById(chapterId);
        return ResponseEntity.ok(chapter);
    }
    
    @PutMapping("/{chapterId}")
    public ResponseEntity<Chapter> updateChapter(
            @PathVariable Long chapterId,
            @RequestBody ChapterRequest request) {
        Chapter chapter = chapterService.updateChapter(
            chapterId,
            request.getTitle(),
            request.getContent()
        );
        return ResponseEntity.ok(chapter);
    }
    
    @DeleteMapping("/{chapterId}")
    public ResponseEntity<Void> deleteChapter(@PathVariable Long chapterId) {
        chapterService.deleteChapter(chapterId);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/count")
    public ResponseEntity<Long> getChapterCount(@PathVariable Long bookId) {
        Book book = bookService.getBookById(bookId);
        Long count = chapterService.getChapterCountByBook(book);
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/first")
    public ResponseEntity<Chapter> getFirstChapter(@PathVariable Long bookId) {
        Book book = bookService.getBookById(bookId);
        Chapter chapter = chapterService.getFirstChapter(book);
        return chapter != null 
            ? ResponseEntity.ok(chapter) 
            : ResponseEntity.notFound().build();
    }
    
    @GetMapping("/{chapterId}/next")
    public ResponseEntity<Chapter> getNextChapter(@PathVariable Long chapterId) {
        Chapter current = chapterService.getChapterById(chapterId);
        Chapter next = chapterService.getNextChapter(current);
        return next != null 
            ? ResponseEntity.ok(next) 
            : ResponseEntity.notFound().build();
    }
    
    @GetMapping("/{chapterId}/previous")
    public ResponseEntity<Chapter> getPreviousChapter(@PathVariable Long chapterId) {
        Chapter current = chapterService.getChapterById(chapterId);
        Chapter prev = chapterService.getPreviousChapter(current);
        return prev != null 
            ? ResponseEntity.ok(prev) 
            : ResponseEntity.notFound().build();
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<Chapter>> searchChapters(
            @PathVariable Long bookId,
            @RequestParam String query) {
        Book book = bookService.getBookById(bookId);
        List<Chapter> chapters = chapterService.searchChaptersInBook(book, query);
        return ResponseEntity.ok(chapters);
    }
}

class ChapterRequest {
    private String title;
    private String content;
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}