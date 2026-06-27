package com.example.catlib.model;

import java.time.LocalDateTime;
import java.util.List;

public record StoredTopicContent(
        String topic,
        String catImageUrl,
        List<BookMetadata> books,
        LocalDateTime storedAt
) {
}