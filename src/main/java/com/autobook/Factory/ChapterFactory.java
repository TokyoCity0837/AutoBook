package com.autobook.Factory;

import com.autobook.Library.Book.Book;
import com.autobook.Library.Chapter.Chapter;
import org.springframework.stereotype.Component;

@Component
public class ChapterFactory {

    public Chapter create(Book book, String title, String content) {
        Chapter chapter = new Chapter();
        chapter.setBook(book);
        chapter.setTitle(title);
        chapter.setContent(content);
        return chapter;
    }
}