package com.example.catlib;

import com.example.catlib.exception.ExternalApiException;
import com.example.catlib.model.BookMetadata;
import com.example.catlib.model.CatResponse;
import com.example.catlib.model.StoredTopicContent;
import com.example.catlib.model.StoredTopicSummary;
import com.example.catlib.service.CatService;
import com.example.catlib.service.LocalStorageService;
import com.example.catlib.service.OpenLibraryService;
import com.example.catlib.service.TopicContentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TopicContentServiceTest {

    @Mock
    private CatService catService;

    @Mock
    private OpenLibraryService openLibraryService;

    @Mock
    private LocalStorageService localStorageService;

    @InjectMocks
    private TopicContentService topicContentService;

    @Test
    void topicContentSuccessfulTest() {
        String topic = "space";

        CatResponse catResponse = new CatResponse(topic, "https://cataas.com/cat/abc123");

        List<BookMetadata> books = List.of(
                new BookMetadata("Space", "Tuba Çınar", 2001, "/key"),
                new BookMetadata("Space 2", "Alperen Çınar", 2010, "/key2")
        );

        when(catService.fetchCatByTag(topic)).thenReturn(catResponse);
        when(openLibraryService.fetchBooksByTopic(topic)).thenReturn(books);

        StoredTopicContent result = topicContentService.fetchAndStoreTopicContent(topic);

        assertEquals(topic, result.topic());
        assertEquals(catResponse.getImageUrl(), result.catImageUrl());
        assertEquals(books, result.books());
        assertNotNull(result.storedAt());

        verify(catService).fetchCatByTag(topic);
        verify(openLibraryService).fetchBooksByTopic(topic);
        verify(localStorageService).save(result);
    }

    @Test
    void summarySuccessfulTest() {
        List<StoredTopicSummary> summaries = List.of(
                new StoredTopicSummary("space", "https://cataas.com/cat/abc123", 2, java.time.LocalDateTime.now())
        );

        when(localStorageService.findAllSummaries()).thenReturn(summaries);

        List<StoredTopicSummary> result = topicContentService.getStoredTopicSummaries();

        assertEquals(summaries, result);

        verify(localStorageService).findAllSummaries();
    }

    @Test
    void catServiceFailTest() {
        String topic = "space";

        when(catService.fetchCatByTag(topic))
                .thenThrow(new ExternalApiException("CATAAS failed"));

        ExternalApiException exception = assertThrows(
                ExternalApiException.class,
                () -> topicContentService.fetchAndStoreTopicContent(topic)
        );

        assertEquals("CATAAS failed", exception.getMessage());

        verify(catService).fetchCatByTag(topic);
    }

    @Test
    void openLibraryFailTest() {
        String topic = "space";

        CatResponse catResponse = new CatResponse(topic, "https://cataas.com/cat/abc123");

        when(catService.fetchCatByTag(topic)).thenReturn(catResponse);
        when(openLibraryService.fetchBooksByTopic(topic))
                .thenThrow(new ExternalApiException("Open Library failed"));

        ExternalApiException exception = assertThrows(
                ExternalApiException.class,
                () -> topicContentService.fetchAndStoreTopicContent(topic)
        );

        assertEquals("Open Library failed", exception.getMessage());

        verify(catService).fetchCatByTag(topic);
        verify(openLibraryService).fetchBooksByTopic(topic);
    }
}