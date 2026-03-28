package com.autobook.Exception;

public class ChapterNotFoundException extends RuntimeException {

    public ChapterNotFoundException(Long chapterId) {
        super("Chapter not found with id: " + chapterId);
    }
}