package com.autobook.Library.Chapter;

import com.autobook.Library.Chapter.DTO.Response.ChapterCardResponse;
import com.autobook.Library.Chapter.DTO.Response.ChapterResponse;
import org.springframework.stereotype.Component;

import com.autobook.Generic.GenericMapper;

@Component
public class ChapterMapper implements GenericMapper<Chapter, ChapterCardResponse> {

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