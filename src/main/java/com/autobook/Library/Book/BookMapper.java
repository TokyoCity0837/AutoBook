package com.autobook.Library.Book;

import com.autobook.Library.Book.DTO.Response.BookCardResponse;
import com.autobook.Library.Book.DTO.Response.BookDetailsResponse;
import com.autobook.Library.Edit.DTO.Response.EditResponse;
import com.autobook.Social.User.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import com.autobook.Generic.GenericMapper;

/**
 * Mapper class for mapping Book entities to various data transfer objects
 * (DTOs).
 * Used primarily for translating database representations into API responses.
 */
@Component
@RequiredArgsConstructor
public class BookMapper implements GenericMapper<Book, BookCardResponse> {

    private final UserMapper userMapper;

    public BookCardResponse toCardResponse(Book book) {
        return new BookCardResponse(
                book.getId(),
                book.getTitle(),
                book.getCoverImage(),
                userMapper.toCardResponse(book.getAuthor()),
                book.getPrivacy());
    }

    public BookDetailsResponse toDetailsResponse(
            Book book,
            List<EditResponse> editRequests) {
        return new BookDetailsResponse(
                book.getId(),
                book.getTitle(),
                book.getCoverImage(),
                userMapper.toCardResponse(book.getAuthor()),
                book.getDescription(),
                book.getGenre(),
                book.getPrivacy(),
                book.getCreatedAt(),
                book.getUpdatedAt(),
                editRequests);
    }
}