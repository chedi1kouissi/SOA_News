package com.example.SOA_News.service;

import com.example.SOA_News.model.User;
import com.example.SOA_News.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Create new user - password stored as plain text
     */
    public User createUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        return userRepository.save(user);
    }
    
    /**
     * Simple authentication - just compare username and password
     */
    public boolean authenticateUser(String username, String password) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // Simple string comparison
            return user.getPassword().equals(password);
        }
        return false;
    }
    
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    /**
     * Save article with category and country
     * Format: "url|category|country"
     */
    public User saveArticle(Long userId, String articleUrl, String category, String country) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        String articleEntry = articleUrl + "|" + category + "|" + country;
        
        // Check if already saved
        boolean alreadySaved = user.getSavedArticleUrls().stream()
                .anyMatch(entry -> entry.startsWith(articleUrl + "|"));
        
        if (alreadySaved) {
            throw new RuntimeException("Article already saved");
        }
        
        user.getSavedArticleUrls().add(articleEntry);
        user.getPreferredCategories().add(category);
        
        if (user.getPreferredCountry().equals("us") && !country.equals("us")) {
            user.setPreferredCountry(country);
        }
        
        return userRepository.save(user);
    }
    
    /**
     * Remove saved article
     */
    public User unsaveArticle(Long userId, String articleUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.getSavedArticleUrls().removeIf(entry -> entry.startsWith(articleUrl + "|"));
        return userRepository.save(user);
    }
    
    /**
     * Get all saved articles with metadata
     */
    public List<Map<String, String>> getSavedArticles(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return user.getSavedArticleUrls().stream()
                .map(entry -> {
                    String[] parts = entry.split("\\|");
                    Map<String, String> article = new HashMap<>();
                    article.put("url", parts[0]);
                    article.put("category", parts.length > 1 ? parts[1] : "general");
                    article.put("country", parts.length > 2 ? parts[2] : "us");
                    return article;
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Get top 3 categories from saved articles
     */
    public List<String> getTop3Categories(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<String> categories = user.getSavedArticleUrls().stream()
                .map(entry -> {
                    String[] parts = entry.split("\\|");
                    return parts.length > 1 ? parts[1] : "general";
                })
                .collect(Collectors.toList());
        
        // Count occurrences
        Map<String, Long> categoryCount = categories.stream()
                .collect(Collectors.groupingBy(c -> c, Collectors.counting()));
        
        // Return top 3
        return categoryCount.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
    
    /**
     * Get user preferences
     */
    public Map<String, Object> getUserPreferences(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Map<String, Object> preferences = new HashMap<>();
        preferences.put("username", user.getUsername());
        preferences.put("preferredCategories", user.getPreferredCategories());
        preferences.put("preferredCountry", user.getPreferredCountry());
        preferences.put("top3Categories", getTop3Categories(userId));
        preferences.put("totalSavedArticles", user.getSavedArticleUrls().size());
        
        return preferences;
    }
    
    /**
     * Check if article is saved
     */
    public boolean isArticleSaved(Long userId, String articleUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return user.getSavedArticleUrls().stream()
                .anyMatch(entry -> entry.startsWith(articleUrl + "|"));
    }
}
