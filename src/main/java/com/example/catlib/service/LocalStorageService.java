package com.example.catlib.service;

import com.example.catlib.exception.ErrorMessages;
import com.example.catlib.exception.StorageException;
import com.example.catlib.model.StoredTopicContent;
import com.example.catlib.model.StoredTopicSummary;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

@Service
public class LocalStorageService {

    private static final Path STORAGE_DIRECTORY = Paths.get("storage");
    private static final String JSON_EXTENSION = ".json";

    private final ObjectMapper objectMapper;

    public LocalStorageService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        createStorageDirectoryIfNeeded();
    }

    public void save(StoredTopicContent content) {
        try {
            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(buildFilePath(content.topic()).toFile(), content);
        } catch (IOException e) {
            throw new StorageException(ErrorMessages.STORAGE_WRITE_FAILED, e);
        }
    }

    public List<StoredTopicSummary> findAllSummaries() {
        try (Stream<Path> files = Files.list(STORAGE_DIRECTORY)) {
            return files
                    .filter(this::isJsonFile)
                    .map(this::readTopicContent)
                    .map(this::toSummary)
                    .toList();
        } catch (IOException e) {
            throw new StorageException(ErrorMessages.STORAGE_READ_FAILED, e);
        }
    }

    private void createStorageDirectoryIfNeeded() {
        try {
            Files.createDirectories(STORAGE_DIRECTORY);
        } catch (IOException e) {
            throw new StorageException(ErrorMessages.STORAGE_CREATE_DIRECTORY_FAILED, e);
        }
    }

    private Path buildFilePath(String topic) {
        return STORAGE_DIRECTORY.resolve(topic + JSON_EXTENSION);
    }

    private boolean isJsonFile(Path path) {
        return Files.isRegularFile(path)
                && path.getFileName().toString().endsWith(JSON_EXTENSION);
    }

    private StoredTopicContent readTopicContent(Path path) {
        try {
            return objectMapper.readValue(path.toFile(), StoredTopicContent.class);
        } catch (IOException e) {
            throw new StorageException(ErrorMessages.STORAGE_READ_FAILED, e);
        }
    }

    private StoredTopicSummary toSummary(StoredTopicContent content) {
        return new StoredTopicSummary(
                content.topic(),
                content.catImageUrl(),
                content.books().size(),
                content.storedAt()
        );
    }
}