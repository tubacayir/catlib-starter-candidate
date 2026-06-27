package com.example.catlib.service;

import com.example.catlib.exception.ErrorMessages;
import com.example.catlib.exception.ExternalApiException;
import com.example.catlib.model.CatResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class CatService {

    private static final String CATAAS_BASE_URL = "https://cataas.com";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public CatService(HttpClient httpClient, ObjectMapper objectMapper) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    public CatResponse fetchCatByTag(String tag) {
        String responseBody = sendGetRequest(buildCatUri(tag));
        String catId = extractCatId(responseBody);
        return new CatResponse(tag, buildImageUrl(catId));
    }

    private URI buildCatUri(String tag) {
        return UriComponentsBuilder
                .fromHttpUrl(CATAAS_BASE_URL)
                .pathSegment("cat", tag)
                .queryParam("json", "true")
                .build()
                .encode()
                .toUri();

    }

    private String sendGetRequest(URI uri) {
        try {
            HttpResponse<String> response = httpClient.send(buildGetRequest(uri), HttpResponse.BodyHandlers.ofString());
            validateSuccessfulResponse(response);
            return response.body();
        } catch (IOException e) {
            throw new ExternalApiException(ErrorMessages.CATAAS_RESPONSE_READ_FAILED, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ExternalApiException(ErrorMessages.CATAAS_REQUEST_INTERRUPTED, e);
        }
    }

    private HttpRequest buildGetRequest(URI uri) {
        return HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

    }

    private void validateSuccessfulResponse(HttpResponse<String> response) {
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new ExternalApiException(ErrorMessages.CATAAS_UNSUCCESSFUL_STATUS + response.statusCode());
        }
    }

    private String extractCatId(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            String catId = root.path("id").asText(null);

            if (!StringUtils.hasText(catId)) {
                throw new ExternalApiException(ErrorMessages.CATAAS_MISSING_CAT_ID);
            }

            return catId;
        } catch (IOException e) {
            throw new ExternalApiException(ErrorMessages.CATAAS_RESPONSE_PARSE_FAILED, e);
        }
    }

    private String buildImageUrl(String catId) {
        return CATAAS_BASE_URL + "/cat/" + catId;
    }
}