package com.example.catlib.model;

import java.time.LocalDateTime;

public record StoredTopicSummary(
        String topic,
        String catImageUrl,
        int bookCount,
        LocalDateTime storedAt
) {
}