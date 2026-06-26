package com.example.catlib.service;

import com.example.catlib.model.CatResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class CatService {
    private static final String CATAAS_BASE_URL = "https://cataas.com";

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public CatResponse fetchCatByTag(String tag) throws Exception {
        String url = CATAAS_BASE_URL + "/cat/" + tag + "?json=true";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        JsonNode root = objectMapper.readTree(response.body());

        String imageUrl = CATAAS_BASE_URL + "/cat/" + root.path("id").asText();

        return new CatResponse(tag, imageUrl);
    }
}
