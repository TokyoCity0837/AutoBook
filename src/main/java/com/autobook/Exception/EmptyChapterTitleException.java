package com.autobook.Exception;

public class EmptyChapterTitleException extends RuntimeException {

    public EmptyChapterTitleException() {
        super("Chapter title must not be empty");
    }
}