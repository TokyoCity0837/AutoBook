package com.autobook.MapperTest;

import com.autobook.Enum.PrivacyType;
import com.autobook.Library.Book.Book;
import com.autobook.Library.Book.BookMapper;
import com.autobook.Library.Book.DTO.Response.BookCardResponse;
import com.autobook.Library.Book.DTO.Response.BookDetailsResponse;
import com.autobook.Social.User.DTO.Response.UserCardResponse;
import com.autobook.Social.User.User;
import com.autobook.Social.User.UserMapper;
import com.autobook.util.UserTestBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookMapperTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private BookMapper bookMapper;

    @Test
    void toCardResponse() {
        User author = new UserTestBuilder().withId(1L).withUsername("andrii").build();
        Book book = new Book();
        book.setId(100L);
        book.setTitle("My Book");
        book.setAuthor(author);
        book.setPrivacy(PrivacyType.PUBLIC);

        UserCardResponse userCard = new UserCardResponse(1L, "Andrii", "andrii", null, null, false);
        when(userMapper.toCardResponse(author)).thenReturn(userCard);

        BookCardResponse response = bookMapper.toCardResponse(book);

        assertNotNull(response);
        assertEquals(100L, response.id());
        assertEquals("My Book", response.title());
        assertEquals(PrivacyType.PUBLIC, response.privacy());
    }

    @Test
    void toDetailsResponse() {
        User author = new UserTestBuilder().withId(1L).withUsername("andrii").build();
        Book book = new Book();
        book.setId(100L);
        book.setTitle("My Book");
        book.setDescription("Description");
        book.setAuthor(author);
        book.setPrivacy(PrivacyType.PUBLIC);

        UserCardResponse userCard = new UserCardResponse(1L, "Andrii", "andrii", null, null, false);
        when(userMapper.toCardResponse(author)).thenReturn(userCard);

        BookDetailsResponse response = bookMapper.toDetailsResponse(book, Collections.emptyList());

        assertNotNull(response);
        assertEquals("My Book", response.title());
        assertEquals("Description", response.description());
        assertEquals(0, response.editRequests().size());
    }
}
