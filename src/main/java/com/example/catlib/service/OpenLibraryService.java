package com.example.catlib.service;

import com.example.catlib.exception.ErrorMessages;
import com.example.catlib.exception.ExternalApiException;
import com.example.catlib.model.BookMetadata;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Service
public class OpenLibraryService {

    private static final String OPEN_LIBRARY_BASE_URL = "https://openlibrary.org";
    private static final String OPEN_LIBRARY_API_NAME = "Open Library";
    private static final int BOOK_LIMIT = 5;

    private final HttpClientService httpClientService;
    private final ObjectMapper objectMapper;

    public OpenLibraryService(HttpClientService httpClientService, ObjectMapper objectMapper) {
        this.httpClientService = httpClientService;
        this.objectMapper = objectMapper;
    }

    public List<BookMetadata> fetchBooksByTopic(String topic) {
        String responseBody = httpClientService.sendGetRequest(buildSearchUri(topic), OPEN_LIBRARY_API_NAME);
        return extractBooks(responseBody);
    }

    private URI buildSearchUri(String topic) {
        return UriComponentsBuilder
                .fromHttpUrl(OPEN_LIBRARY_BASE_URL)
                .path("/search.json")
                .queryParam("q", topic)
                .queryParam("fields", "key,title,author_name,first_publish_year")
                .queryParam("limit", BOOK_LIMIT)
                .build()
                .encode()
                .toUri();
    }

    private List<BookMetadata> extractBooks(String responseBody) {
        try {
            JsonNode docs = objectMapper.readTree(responseBody).path("docs");
            List<BookMetadata> books = new ArrayList<>();

            if (!docs.isArray()) {
                return books;
            }

            docs.forEach(doc -> books.add(toBookMetadata(doc)));

            return books;
        } catch (IOException e) {
            throw new ExternalApiException(ErrorMessages.OPEN_LIBRARY_RESPONSE_PARSE_FAILED, e);
        }
    }

    private BookMetadata toBookMetadata(JsonNode doc) {
        return new BookMetadata(
                doc.path("title").asText(null),
                extractFirstAuthor(doc),
                extractFirstPublishYear(doc),
                doc.path("key").asText(null)
        );
    }

    private String extractFirstAuthor(JsonNode doc) {
        JsonNode authors = doc.path("author_name");

        if (!authors.isArray() || authors.isEmpty()) {
            return null;
        }

        return authors.get(0).asText(null);
    }

    private Integer extractFirstPublishYear(JsonNode doc) {
        JsonNode year = doc.path("first_publish_year");

        if (year.isMissingNode() || year.isNull()) {
            return null;
        }

        return year.asInt();
    }
}