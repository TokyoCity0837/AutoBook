package com.autobook.ServiceTest;

import com.autobook.Exception.ChapterNotFoundException;
import com.autobook.Exception.EmptyChapterTitleException;
import com.autobook.Factory.ChapterFactory;
import com.autobook.Library.Book.Book;
import com.autobook.Library.Chapter.*;
import com.autobook.Library.Chapter.DTO.Request.ChapterCreateRequest;
import com.autobook.Library.Chapter.DTO.Request.ChapterUpdateRequest;
import com.autobook.Library.Chapter.DTO.Response.ChapterCardResponse;
import com.autobook.Library.Chapter.DTO.Response.ChapterResponse;
import com.autobook.util.BookTestBuilder;
import com.autobook.util.ChapterTestBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChapterServiceTest {

        @Mock
        private ChapterRepository chapterRepository;

        @Mock
        private ChapterFactory chapterFactory;

        @Mock
        private ChapterMapper chapterMapper;

        @InjectMocks
        private ChapterService chapterService;

        @Test
        void createChapter_ok() {
                Book book = new BookTestBuilder().build();

                ChapterCreateRequest request = new ChapterCreateRequest("Intro", "Hello world");

                Chapter chapter = new ChapterTestBuilder()
                                .withBook(book)
                                .withTitle("Intro")
                                .withContent("Hello world")
                                .build();

                ChapterResponse response = new ChapterResponse(
                                1L,
                                "Intro",
                                "Hello world",
                                LocalDateTime.now(),
                                LocalDateTime.now());

                when(chapterFactory.create(book, "Intro", "Hello world")).thenReturn(chapter);
                when(chapterRepository.save(chapter)).thenReturn(chapter);
                when(chapterMapper.toResponse(chapter)).thenReturn(response);

                ChapterResponse result = chapterService.createChapter(book, request);

                assertNotNull(result);
                assertEquals("Intro", result.title());
                assertEquals("Hello world", result.content());

                verify(chapterFactory).create(book, "Intro", "Hello world");
                verify(chapterRepository).save(chapter);
                verify(chapterMapper).toResponse(chapter);
        }

        @Test
        void createChapter_emptyTitle() {
                Book book = new BookTestBuilder().build();
                ChapterCreateRequest request = new ChapterCreateRequest(" ", "Hello world");

                assertThrows(
                                EmptyChapterTitleException.class,
                                () -> chapterService.createChapter(book, request));

                verify(chapterFactory, never()).create(any(), any(), any());
                verify(chapterRepository, never()).save(any(Chapter.class));
        }

        @Test
        void createChapter_nullTitle() {
                Book book = new BookTestBuilder().build();
                ChapterCreateRequest request = new ChapterCreateRequest(null, "Hello world");

                assertThrows(
                                EmptyChapterTitleException.class,
                                () -> chapterService.createChapter(book, request));

                verify(chapterFactory, never()).create(any(), any(), any());
                verify(chapterRepository, never()).save(any(Chapter.class));
        }

        @Test
        void getChapterById_ok() {
                Chapter chapter = new ChapterTestBuilder()
                                .withId(1L)
                                .withTitle("Intro")
                                .build();

                ChapterResponse response = new ChapterResponse(
                                1L,
                                "Intro",
                                chapter.getContent(),
                                chapter.getCreatedAt(),
                                chapter.getUpdatedAt());

                when(chapterRepository.findById(1L)).thenReturn(Optional.of(chapter));
                when(chapterMapper.toResponse(chapter)).thenReturn(response);

                ChapterResponse result = chapterService.getChapterById(1L);

                assertEquals(1L, result.id());
                assertEquals("Intro", result.title());

                verify(chapterRepository).findById(1L);
                verify(chapterMapper).toResponse(chapter);
        }

        @Test
        void getChapterById_notFound() {
                when(chapterRepository.findById(1L)).thenReturn(Optional.empty());

                assertThrows(
                                ChapterNotFoundException.class,
                                () -> chapterService.getChapterById(1L));

                verify(chapterRepository).findById(1L);
        }

        @Test
        void getChaptersByBook_ok() {
                Book book = new BookTestBuilder().build();

                Chapter chapter1 = new ChapterTestBuilder()
                                .withId(1L)
                                .withBook(book)
                                .withTitle("Intro")
                                .build();

                Chapter chapter2 = new ChapterTestBuilder()
                                .withId(2L)
                                .withBook(book)
                                .withTitle("Ending")
                                .build();

                ChapterCardResponse response1 = new ChapterCardResponse(1L, "Intro");
                ChapterCardResponse response2 = new ChapterCardResponse(2L, "Ending");

                when(chapterRepository.findByBook(book)).thenReturn(List.of(chapter1, chapter2));
                when(chapterMapper.toCardResponse(chapter1)).thenReturn(response1);
                when(chapterMapper.toCardResponse(chapter2)).thenReturn(response2);

                List<ChapterCardResponse> result = chapterService.getChaptersByBook(book);

                assertEquals(2, result.size());
                assertEquals("Intro", result.get(0).title());
                assertEquals("Ending", result.get(1).title());

                verify(chapterRepository).findByBook(book);
                verify(chapterMapper).toCardResponse(chapter1);
                verify(chapterMapper).toCardResponse(chapter2);
        }

        @Test
        void getChaptersByBookOrdered_ok() {
                Book book = new BookTestBuilder().build();

                Chapter chapter1 = new ChapterTestBuilder()
                                .withId(1L)
                                .withBook(book)
                                .withTitle("Chapter 1")
                                .build();

                Chapter chapter2 = new ChapterTestBuilder()
                                .withId(2L)
                                .withBook(book)
                                .withTitle("Chapter 2")
                                .build();

                ChapterResponse response1 = new ChapterResponse(1L, "Chapter 1", "Content 1", LocalDateTime.now(),
                                LocalDateTime.now());
                ChapterResponse response2 = new ChapterResponse(2L, "Chapter 2", "Content 2", LocalDateTime.now(),
                                LocalDateTime.now());

                when(chapterRepository.findByBookOrderByCreatedAtAsc(book))
                                .thenReturn(List.of(chapter1, chapter2));
                when(chapterMapper.toResponse(chapter1)).thenReturn(response1);
                when(chapterMapper.toResponse(chapter2)).thenReturn(response2);

                List<ChapterResponse> result = chapterService.getChaptersByBookOrdered(book);

                assertEquals(2, result.size());
                assertEquals("Chapter 1", result.get(0).title());
                assertEquals("Chapter 2", result.get(1).title());

                verify(chapterRepository).findByBookOrderByCreatedAtAsc(book);
                verify(chapterMapper).toResponse(chapter1);
                verify(chapterMapper).toResponse(chapter2);
        }

        @Test
        void countChaptersByBook_ok() {
                Book book = new BookTestBuilder().build();

                when(chapterRepository.countByBook(book)).thenReturn(3L);

                Long result = chapterService.countChaptersByBook(book);

                assertEquals(3L, result);
                verify(chapterRepository).countByBook(book);
        }

        @Test
        void searchChaptersByContent_ok() {
                Chapter chapter1 = new ChapterTestBuilder()
                                .withId(1L)
                                .withTitle("Intro")
                                .withContent("Java basics")
                                .build();

                Chapter chapter2 = new ChapterTestBuilder()
                                .withId(2L)
                                .withTitle("Advanced")
                                .withContent("More Java")
                                .build();

                ChapterResponse response1 = new ChapterResponse(
                                1L, "Intro", "Java basics",
                                chapter1.getCreatedAt(), chapter1.getUpdatedAt());

                ChapterResponse response2 = new ChapterResponse(
                                2L, "Advanced", "More Java",
                                chapter2.getCreatedAt(), chapter2.getUpdatedAt());

                when(chapterRepository.findByContentContainingIgnoreCase("java"))
                                .thenReturn(List.of(chapter1, chapter2));
                when(chapterMapper.toResponse(chapter1)).thenReturn(response1);
                when(chapterMapper.toResponse(chapter2)).thenReturn(response2);

                List<ChapterResponse> result = chapterService.searchChaptersByContent("java");

                assertEquals(2, result.size());
                assertEquals("Java basics", result.get(0).content());
                assertEquals("More Java", result.get(1).content());

                verify(chapterRepository).findByContentContainingIgnoreCase("java");
                verify(chapterMapper).toResponse(chapter1);
                verify(chapterMapper).toResponse(chapter2);
        }

        @Test
        void updateChapter_ok_updateTitleAndContent() {
                Chapter chapter = new ChapterTestBuilder()
                                .withId(1L)
                                .withTitle("Old title")
                                .withContent("Old content")
                                .build();

                ChapterUpdateRequest request = new ChapterUpdateRequest("New title", "New content");

                ChapterResponse response = new ChapterResponse(
                                1L,
                                "New title",
                                "New content",
                                chapter.getCreatedAt(),
                                chapter.getUpdatedAt());

                when(chapterRepository.findById(1L)).thenReturn(Optional.of(chapter));
                when(chapterRepository.save(chapter)).thenReturn(chapter);
                when(chapterMapper.toResponse(chapter)).thenReturn(response);

                ChapterResponse result = chapterService.updateChapter(1L, request);

                assertEquals("New title", result.title());
                assertEquals("New content", result.content());

                verify(chapterRepository).findById(1L);
                verify(chapterRepository).save(chapter);
                verify(chapterMapper).toResponse(chapter);
        }

        @Test
        void updateChapter_ok_onlyTitle() {
                Chapter chapter = new ChapterTestBuilder()
                                .withId(1L)
                                .withTitle("Old title")
                                .withContent("Old content")
                                .build();

                ChapterUpdateRequest request = new ChapterUpdateRequest("New title", null);

                ChapterResponse response = new ChapterResponse(
                                1L,
                                "New title",
                                "Old content",
                                chapter.getCreatedAt(),
                                chapter.getUpdatedAt());

                when(chapterRepository.findById(1L)).thenReturn(Optional.of(chapter));
                when(chapterRepository.save(chapter)).thenReturn(chapter);
                when(chapterMapper.toResponse(chapter)).thenReturn(response);

                ChapterResponse result = chapterService.updateChapter(1L, request);

                assertEquals("New title", result.title());
                assertEquals("Old content", result.content());

                verify(chapterRepository).findById(1L);
                verify(chapterRepository).save(chapter);
                verify(chapterMapper).toResponse(chapter);
        }

        @Test
        void updateChapter_ok_onlyContent() {
                Chapter chapter = new ChapterTestBuilder()
                                .withId(1L)
                                .withTitle("Old title")
                                .withContent("Old content")
                                .build();

                ChapterUpdateRequest request = new ChapterUpdateRequest(null, "New content");

                ChapterResponse response = new ChapterResponse(
                                1L,
                                "Old title",
                                "New content",
                                chapter.getCreatedAt(),
                                chapter.getUpdatedAt());

                when(chapterRepository.findById(1L)).thenReturn(Optional.of(chapter));
                when(chapterRepository.save(chapter)).thenReturn(chapter);
                when(chapterMapper.toResponse(chapter)).thenReturn(response);

                ChapterResponse result = chapterService.updateChapter(1L, request);

                assertEquals("Old title", result.title());
                assertEquals("New content", result.content());

                verify(chapterRepository).findById(1L);
                verify(chapterRepository).save(chapter);
                verify(chapterMapper).toResponse(chapter);
        }

        @Test
        void updateChapter_notFound() {
                ChapterUpdateRequest request = new ChapterUpdateRequest("New title", "New content");

                when(chapterRepository.findById(1L)).thenReturn(Optional.empty());

                assertThrows(
                                ChapterNotFoundException.class,
                                () -> chapterService.updateChapter(1L, request));

                verify(chapterRepository).findById(1L);
                verify(chapterRepository, never()).save(any(Chapter.class));
        }

        @Test
        void deleteChapter_ok() {
                Chapter chapter = new ChapterTestBuilder()
                                .withId(1L)
                                .build();

                when(chapterRepository.findById(1L)).thenReturn(Optional.of(chapter));

                chapterService.deleteChapter(1L);

                verify(chapterRepository).findById(1L);
                verify(chapterRepository).delete(chapter);
        }

        @Test
        void deleteChapter_notFound() {
                when(chapterRepository.findById(1L)).thenReturn(Optional.empty());

                assertThrows(
                                ChapterNotFoundException.class,
                                () -> chapterService.deleteChapter(1L));

                verify(chapterRepository).findById(1L);
                verify(chapterRepository, never()).delete(any(Chapter.class));
        }
}