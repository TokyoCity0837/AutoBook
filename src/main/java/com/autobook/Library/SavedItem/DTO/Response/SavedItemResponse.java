package com.autobook.Library.SavedItem.DTO.Response;

import com.autobook.Library.Book.DTO.Response.BookCardResponse;
import com.autobook.Library.SavedItem.SavedItem;

import java.time.LocalDateTime;

public record SavedItemResponse(
        Long id,
        SavedItem.ItemType itemType,
        BookCardResponse book,
        LocalDateTime savedAt
) {}
