package com.example.SOA_News.controller;


import com.example.SOA_News.DTO.NewsApiResponse;
import com.example.SOA_News.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/news")
@CrossOrigin(origins = "*")
public class NewsController {
    
    @Autowired
    private NewsService newsService;
    
    @GetMapping("/top-headlines")
    public ResponseEntity<NewsApiResponse> getTopHeadlines(
            @RequestParam(defaultValue = "us") String country) {
        NewsApiResponse response = newsService.getTopHeadlines(country);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/search")
    public ResponseEntity<NewsApiResponse> searchNews(
            @RequestParam String q) {
        NewsApiResponse response = newsService.searchNews(q);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/category/{category}")
    public ResponseEntity<NewsApiResponse> getNewsByCategory(
            @PathVariable String category) {
        NewsApiResponse response = newsService.getNewsByCategory(category);
        return ResponseEntity.ok(response);
    }
}
