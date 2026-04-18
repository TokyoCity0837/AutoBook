package com.autobook.util;

import com.autobook.Library.Book.Book;
import com.autobook.Library.Chapter.Chapter;

public class ChapterTestBuilder {

    private Long id = 1L;
    private Book book = new BookTestBuilder().build();
    private String title = "Chapter 1";
    private String content = "Chapter content";

    public ChapterTestBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public ChapterTestBuilder withBook(Book book) {
        this.book = book;
        return this;
    }

    public ChapterTestBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    public ChapterTestBuilder withContent(String content) {
        this.content = content;
        return this;
    }

    public Chapter build() {
        Chapter chapter = new Chapter();
        chapter.setId(id);
        chapter.setBook(book);
        chapter.setTitle(title);
        chapter.setContent(content);
        return chapter;
    }
}