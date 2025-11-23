package com.example.SOA_News.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.SOA_News.DTO.NewsApiResponse;

@Service
public class NewsService {
    
    @Value("${news.api.key}")
    private String apiKey;
    
    @Value("${news.api.url}")
    private String apiUrl;
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    public NewsApiResponse getTopHeadlines(String country) {
        String url = String.format("%s/top-headlines?country=%s&apiKey=%s", 
                                   apiUrl, country, apiKey);
        return restTemplate.getForObject(url, NewsApiResponse.class);
    }
    
    public NewsApiResponse searchNews(String query) {
        String url = String.format("%s/everything?q=%s&sortBy=publishedAt&apiKey=%s", 
                                   apiUrl, query, apiKey);
        return restTemplate.getForObject(url, NewsApiResponse.class);
    }
    
    public NewsApiResponse getNewsByCategory(String category) {
        String url = String.format("%s/top-headlines?category=%s&apiKey=%s", 
                                   apiUrl, category, apiKey);
        return restTemplate.getForObject(url, NewsApiResponse.class);
    }
}
