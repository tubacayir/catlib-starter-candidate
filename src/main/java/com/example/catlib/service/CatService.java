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

@Service
public class CatService {

    private static final String CATAAS_BASE_URL = "https://cataas.com";
    private static final String CATAAS_API_NAME = "CATAAS";

    private final HttpClientService httpClientService;
    private final ObjectMapper objectMapper;

    public CatService(HttpClientService httpClientService, ObjectMapper objectMapper) {
        this.httpClientService = httpClientService;
        this.objectMapper = objectMapper;
    }

    public CatResponse fetchCatByTag(String tag) {
        String responseBody = httpClientService.sendGetRequest(buildCatUri(tag), CATAAS_API_NAME);
        String catId = extractCatId(responseBody);

        return new CatResponse(tag, buildImageUrl(catId));
    }

    private URI buildCatUri(String tag) {
        return UriComponentsBuilder
                .fromHttpUrl(CATAAS_BASE_URL)
                .pathSegment("cat", tag)
                .queryParam("json", true)
                .build()
                .encode()
                .toUri();
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