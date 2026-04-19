package com.autobook.Library.Chapter;

import com.autobook.Library.Book.Book;
import com.autobook.Library.Book.BookRepository;
import com.autobook.Exception.BookNotFoundException;
import com.autobook.Library.Chapter.DTO.Request.ChapterCreateRequest;
import com.autobook.Library.Chapter.DTO.Request.ChapterUpdateRequest;
import com.autobook.Library.Chapter.DTO.Response.ChapterCardResponse;
import com.autobook.Library.Chapter.DTO.Response.ChapterResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chapters")
@RequiredArgsConstructor
public class ChapterController {

    private final ChapterService chapterService;
    private final BookRepository bookRepository;

    @PostMapping("/book/{bookId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ChapterResponse createChapter(@PathVariable Long bookId, @RequestBody ChapterCreateRequest request) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));
        return chapterService.createChapter(book, request);
    }

    @GetMapping("/{id}")
    public ChapterResponse getChapter(@PathVariable Long id) {
        return chapterService.getChapterById(id);
    }

    @GetMapping("/book/{bookId}")
    public List<ChapterResponse> getChaptersByBook(@PathVariable Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));
        return chapterService.getChaptersByBookOrdered(book);
    }

    @GetMapping("/book/{bookId}/count")
    public Long countChaptersByBook(@PathVariable Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));
        return chapterService.countChaptersByBook(book);
    }

    @GetMapping("/search")
    public List<ChapterResponse> searchChapters(@RequestParam String content) {
        return chapterService.searchChaptersByContent(content);
    }

    @PutMapping("/{id}")
    public ChapterResponse updateChapter(@PathVariable Long id, @RequestBody ChapterUpdateRequest request) {
        return chapterService.updateChapter(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteChapter(@PathVariable Long id) {
        chapterService.deleteChapter(id);
    }
}
