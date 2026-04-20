package com.autobook.Library.Chapter;

import com.autobook.Exception.ChapterNotFoundException;
import com.autobook.Exception.EmptyChapterTitleException;
import com.autobook.Factory.ChapterFactory;
import com.autobook.Library.Book.Book;
import com.autobook.Library.Chapter.DTO.Request.ChapterCreateRequest;
import com.autobook.Library.Chapter.DTO.Request.ChapterUpdateRequest;
import com.autobook.Library.Chapter.DTO.Response.ChapterCardResponse;
import com.autobook.Library.Chapter.DTO.Response.ChapterResponse;
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
    private final ChapterMapper chapterMapper;

    @Transactional
    public ChapterResponse createChapter(Book book, ChapterCreateRequest request) {
        validateTitle(request.title());

        Chapter chapter = chapterFactory.create(book, request.title(), request.content());
        Chapter savedChapter = chapterRepository.save(chapter);

        return chapterMapper.toResponse(savedChapter);
    }

    public ChapterResponse getChapterById(Long chapterId) {
        return chapterMapper.toResponse(getChapterEntityById(chapterId));
    }

    public List<ChapterCardResponse> getChaptersByBook(Book book) {
        return chapterRepository.findByBook(book)
                .stream()
                .map(chapterMapper::toCardResponse)
                .toList();
    }

    public List<ChapterResponse> getChaptersByBookOrdered(Book book) {
        return chapterRepository.findByBookOrderByCreatedAtAsc(book)
                .stream()
                .map(chapterMapper::toResponse)
                .toList();
    }

    public Long countChaptersByBook(Book book) {
        return chapterRepository.countByBook(book);
    }

    public List<ChapterResponse> searchChaptersByContent(String text) {
        return chapterRepository.findByContentContainingIgnoreCase(text)
                .stream()
                .map(chapterMapper::toResponse)
                .toList();
    }

    @Transactional
    public ChapterResponse updateChapter(Long chapterId, ChapterUpdateRequest request) {
        Chapter chapter = getChapterEntityById(chapterId);

        if (request.title() != null) {
            validateTitle(request.title());
            chapter.setTitle(request.title());
        }

        if (request.content() != null) {
            chapter.setContent(request.content());
        }

        Chapter updatedChapter = chapterRepository.save(chapter);
        return chapterMapper.toResponse(updatedChapter);
    }

    @Transactional
    public void deleteChapter(Long chapterId) {
        Chapter chapter = getChapterEntityById(chapterId);
        chapterRepository.delete(chapter);
    }

    private Chapter getChapterEntityById(Long chapterId) {
        return chapterRepository.findById(chapterId)
                .orElseThrow(() -> new ChapterNotFoundException(chapterId));
    }

    private void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new EmptyChapterTitleException();
        }
    }
}