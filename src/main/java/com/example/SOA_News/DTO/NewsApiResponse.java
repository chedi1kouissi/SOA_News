package com.example.SOA_News.DTO;

import com.example.SOA_News.model.Article;
import java.util.List;

public class NewsApiResponse {
    private String status;
    private int totalResults;
    private List<Article> articles;
    
    // Getters and setters
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public int getTotalResults() { return totalResults; }
    public void setTotalResults(int totalResults) { this.totalResults = totalResults; }
    
    public List<Article> getArticles() { return articles; }
    public void setArticles(List<Article> articles) { this.articles = articles; }
}
