package com.autobook.util;

import com.autobook.Enum.PrivacyType;
import com.autobook.Library.Book.Book;
import com.autobook.Social.User.User;

public class BookTestBuilder {

    private Long id = 1L;
    private User author = new UserTestBuilder().build();
    private String title = "Default Title";
    private String description = "Default description";
    private String genre = "Fantasy";
    private PrivacyType privacy = PrivacyType.PRIVATE;
    private String coverImage = "default-cover.png";

    public BookTestBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public BookTestBuilder withAuthor(User author) {
        this.author = author;
        return this;
    }

    public BookTestBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    public BookTestBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public BookTestBuilder withGenre(String genre) {
        this.genre = genre;
        return this;
    }

    public BookTestBuilder withPrivacy(PrivacyType privacy) {
        this.privacy = privacy;
        return this;
    }

    public BookTestBuilder withCoverImage(String coverImage) {
        this.coverImage = coverImage;
        return this;
    }

    public Book build() {
        Book book = new Book();
        book.setId(id);
        book.setAuthor(author);
        book.setTitle(title);
        book.setDescription(description);
        book.setGenre(genre);
        book.setPrivacy(privacy);
        book.setCoverImage(coverImage);
        return book;
    }
}