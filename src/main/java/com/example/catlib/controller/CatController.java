package com.example.catlib.controller;

import com.example.catlib.model.CatResponse;
import com.example.catlib.service.CatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CatController {

    private final CatService catService;

    public CatController(CatService catService) {
        this.catService = catService;
    }

    /**
     * Returns a cat image URL for the given topic/tag.
     * <p>
     * Example: GET /api/cat/space
     */
    @GetMapping("/cat/{tag}")
    public ResponseEntity<CatResponse> getCatByTag(@PathVariable String tag) {
        return ResponseEntity.ok(catService.fetchCatByTag(tag));
    }
}
