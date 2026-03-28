package com.autobook.Factory;

import com.autobook.Enum.PrivacyType;
import com.autobook.Library.Book.Book;
import com.autobook.Social.User.User;
import org.springframework.stereotype.Component;

@Component
public class BookFactory {

    public Book create(User author,
                       String title,
                       String description,
                       String genre,
                       PrivacyType privacy,
                       String coverImage) {
        Book book = new Book();
        book.setAuthor(author);
        book.setTitle(title);
        book.setDescription(description);
        book.setGenre(genre);
        book.setPrivacy(privacy != null ? privacy : PrivacyType.PRIVATE);
        book.setCoverImage(coverImage);
        return book;
    }
}