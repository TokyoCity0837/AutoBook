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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for managing {@link Chapter} entities associated with a specific
 * {@link Book}.
 *
 * @see ChapterFactory
 * @see Chapter
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChapterService {

    private final ChapterRepository chapterRepository;
    private final ChapterFactory chapterFactory;
    private final ChapterMapper chapterMapper;

    /**
     * Creates a new chapter for an existing book.
     *
     * @param book    the parent book entity
     * @param request the request containing chapter title and content
     * @return the saved chapter DTO
     * @throws EmptyChapterTitleException if the title is invalid
     */
    @Transactional
    public ChapterResponse createChapter(Book book, ChapterCreateRequest request) {
        log.info("Creating new chapter for book ID: {}", book.getId());
        validateTitle(request.title());

        Chapter chapter = chapterFactory.create(book, request.title(), request.content());
        Chapter savedChapter = chapterRepository.save(chapter);

        log.debug("Chapter successfully created with ID: {}", savedChapter.getId());
        return chapterMapper.toResponse(savedChapter);
    }

    /**
     * Retrieves a chapter and converts it into a detailed DTO structure.
     *
     * @param chapterId the target chapter ID
     * @return a structured DTO representing the chapter
     * @throws ChapterNotFoundException if the ID does not match any record
     */
    public ChapterResponse getChapterById(Long chapterId) {
        log.debug("Retrieving chapter by ID: {}", chapterId);
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

    /**
     * Contextually updates an existing chapter allowing partial field mutations.
     *
     * @param chapterId the target chapter
     * @param request   the requested payload updates
     * @return the updated chapter mapped as DTO
     */
    @Transactional
    public ChapterResponse updateChapter(Long chapterId, ChapterUpdateRequest request) {
        log.info("Updating chapter with ID: {}", chapterId);
        Chapter chapter = getChapterEntityById(chapterId);

        if (request.title() != null) {
            validateTitle(request.title());
            chapter.setTitle(request.title());
        }

        if (request.content() != null) {
            chapter.setContent(request.content());
        }

        Chapter updatedChapter = chapterRepository.save(chapter);
        log.debug("Chapter updated successfully");
        return chapterMapper.toResponse(updatedChapter);
    }

    /**
     * Deletes a chapter by its key identifier.
     *
     * @param chapterId the target chapter
     */
    @Transactional
    public void deleteChapter(Long chapterId) {
        log.info("Deleting chapter with ID: {}", chapterId);
        Chapter chapter = getChapterEntityById(chapterId);
        chapterRepository.delete(chapter);
        log.debug("Chapter deleted successfully");
    }

    private Chapter getChapterEntityById(Long chapterId) {
        return chapterRepository.findById(chapterId)
                .orElseThrow(() -> {
                    log.error("Chapter not found for ID: {}", chapterId);
                    return new ChapterNotFoundException(chapterId);
                });
    }

    private void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            log.error("Attempted to set an empty title for a chapter");
            throw new EmptyChapterTitleException();
        }
    }
}