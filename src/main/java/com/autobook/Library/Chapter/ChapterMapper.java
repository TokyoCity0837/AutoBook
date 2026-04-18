package com.autobook.Library.Chapter;

import com.autobook.Library.Chapter.DTO.Response.ChapterCardResponse;
import com.autobook.Library.Chapter.DTO.Response.ChapterResponse;
import org.springframework.stereotype.Component;

@Component
public class ChapterMapper {

    public ChapterCardResponse toCardResponse(Chapter chapter) {
        return new ChapterCardResponse(
                chapter.getId(),
                chapter.getTitle()
        );
    }

    public ChapterResponse toResponse(Chapter chapter) {
        return new ChapterResponse(
                chapter.getId(),
                chapter.getTitle(),
                chapter.getContent(),
                chapter.getCreatedAt(),
                chapter.getUpdatedAt()
        );
    }
}