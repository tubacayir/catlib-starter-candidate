package com.example.catlib.controller;

import com.example.catlib.model.StoredTopicContent;
import com.example.catlib.model.StoredTopicSummary;
import com.example.catlib.service.TopicContentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/topics")
public class TopicController {

    private final TopicContentService topicContentService;

    public TopicController(TopicContentService topicContentService) {
        this.topicContentService = topicContentService;
    }

    @PostMapping("/{topic}")
    public ResponseEntity<StoredTopicContent> createTopicContent(@PathVariable String topic) {
        StoredTopicContent storedContent = topicContentService.fetchAndStoreTopicContent(topic);
        return ResponseEntity.status(HttpStatus.CREATED).body(storedContent);
    }

    @GetMapping("/summary")
    public ResponseEntity<List<StoredTopicSummary>> getStoredTopicSummaries() {
        return ResponseEntity.ok(topicContentService.getStoredTopicSummaries());
    }
}