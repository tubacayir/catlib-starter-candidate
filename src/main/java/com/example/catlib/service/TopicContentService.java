package com.example.catlib.service;

import com.example.catlib.model.BookMetadata;
import com.example.catlib.model.CatResponse;
import com.example.catlib.model.StoredTopicContent;
import com.example.catlib.model.StoredTopicSummary;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TopicContentService {

    private final CatService catService;
    private final OpenLibraryService openLibraryService;
    private final LocalStorageService localStorageService;

    public TopicContentService(CatService catService,
                               OpenLibraryService openLibraryService,
                               LocalStorageService localStorageService) {
        this.catService = catService;
        this.openLibraryService = openLibraryService;
        this.localStorageService = localStorageService;
    }

    public StoredTopicContent fetchAndStoreTopicContent(String topic) {
        CatResponse catResponse = catService.fetchCatByTag(topic);
        List<BookMetadata> books = openLibraryService.fetchBooksByTopic(topic);

        StoredTopicContent content = new StoredTopicContent(
                topic,
                catResponse.getImageUrl(),
                books,
                LocalDateTime.now()
        );

        localStorageService.save(content);

        return content;
    }

    public List<StoredTopicSummary> getStoredTopicSummaries() {
        return localStorageService.findAllSummaries();
    }
}