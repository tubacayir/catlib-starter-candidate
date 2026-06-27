package com.example.catlib.model;


public record BookMetadata(
        String title,
        String author,
        Integer firstPublishYear,
        String openLibraryKey
) {
}


