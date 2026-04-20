package com.autobook.Library.SavedItem;

import com.autobook.Library.Book.DTO.Response.BookCardResponse;
import java.time.LocalDateTime;

public record SavedItemResponse(
        Long id,
        SavedItem.ItemType itemType,
        BookCardResponse book,
        LocalDateTime savedAt
) {}
