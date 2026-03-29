package com.autobook.Library.Chapter;

import com.autobook.Exception.ChapterNotFoundException;
import com.autobook.Exception.EmptyChapterTitleException;
import com.autobook.Factory.ChapterFactory;
import com.autobook.Library.Book.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChapterService {

    private final ChapterRepository chapterRepository;
    private final ChapterFactory chapterFactory;

    @Transactional
    public Chapter createChapter(Book book, String title, String content) {
        validateTitle(title);

        Chapter chapter = chapterFactory.create(book, title, content);
        return chapterRepository.save(chapter);
    }

    public Chapter getChapterById(Long chapterId) {
        return chapterRepository.findById(chapterId)
                .orElseThrow(() -> new ChapterNotFoundException(chapterId));
    }

    public List<Chapter> getChaptersByBook(Book book) {
        return chapterRepository.findByBook(book);
    }

    public List<Chapter> getChaptersByBookOrdered(Book book) {
        return chapterRepository.findByBookOrderByCreatedAtAsc(book);
    }

    public Long countChaptersByBook(Book book) {
        return chapterRepository.countByBook(book);
    }

    public List<Chapter> searchChaptersByContent(String text) {
        return chapterRepository.findByContentContainingIgnoreCase(text);
    }

    @Transactional
    public Chapter updateChapter(Long chapterId, String title, String content) {
        Chapter chapter = getChapterById(chapterId);

        if (title != null) {
            validateTitle(title);
            chapter.setTitle(title);
        }

        if (content != null) {
            chapter.setContent(content);
        }

        return chapterRepository.save(chapter);
    }

    @Transactional
    public void deleteChapter(Long chapterId) {
        Chapter chapter = getChapterById(chapterId);
        chapterRepository.delete(chapter);
    }

    private void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new EmptyChapterTitleException();
        }
    }
}