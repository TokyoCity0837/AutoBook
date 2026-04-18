package com.autobook.Library.Book;

import com.autobook.Library.Book.DTO.Response.BookCardResponse;
import com.autobook.Library.Book.DTO.Response.BookDetailsResponse;
import com.autobook.Library.Edit.DTO.Response.EditResponse;
import com.autobook.Social.User.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BookMapper {

    private final UserMapper userMapper;

    public BookCardResponse toCardResponse(Book book) {
        return new BookCardResponse(
                book.getId(),
                book.getTitle(),
                book.getCoverImage(),
                userMapper.toCardResponse(book.getAuthor())
        );
    }

    public BookDetailsResponse toDetailsResponse(
            Book book,
            List<EditResponse> editRequests
    ) {
        return new BookDetailsResponse(
                book.getId(),
                book.getTitle(),
                book.getCoverImage(),
                userMapper.toCardResponse(book.getAuthor()),
                book.getDescription(),
                editRequests
        );
    }
}