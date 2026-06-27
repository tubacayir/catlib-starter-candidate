package com.example.catlib.service;

import com.example.catlib.exception.ExternalApiException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class HttpClientService {

    private final HttpClient httpClient;

    public HttpClientService(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public String sendGetRequest(URI uri, String apiName) {
        try {
            HttpResponse<String> response = httpClient.send(buildGetRequest(uri), HttpResponse.BodyHandlers.ofString());
            validateSuccessfulResponse(response, apiName);
            return response.body();
        } catch (IOException e) {
            throw new ExternalApiException(apiName + " API response could not be read.", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ExternalApiException(apiName + " API request was interrupted.", e);
        }
    }

    private HttpRequest buildGetRequest(URI uri) {
        return HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
    }

    private void validateSuccessfulResponse(HttpResponse<String> response, String apiName) {
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return;
        }

        throw new ExternalApiException(apiName + " API returned an unsuccessful status: " + response.statusCode());
    }
}