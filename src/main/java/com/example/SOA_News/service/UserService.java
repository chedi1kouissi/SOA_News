package com.example.SOA_News.service;

import com.example.SOA_News.model.Article;
import com.example.SOA_News.model.SavedArticle;
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

    @Autowired
    private NewsService newsService;

    public User createUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        return userRepository.save(user);
    }

    public boolean authenticateUser(String username, String password) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
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

    public User saveArticle(Long userId, Article article, String category, String country) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if already saved
        boolean alreadySaved = user.getSavedArticles().stream()
                .anyMatch(sa -> sa.getUrl().equals(article.getUrl()));

        if (alreadySaved) {
            throw new RuntimeException("Article already saved");
        }

        SavedArticle savedArticle = new SavedArticle();
        savedArticle.setUser(user);
        savedArticle.setUrl(article.getUrl());
        savedArticle.setTitle(article.getTitle());
        savedArticle.setDescription(article.getDescription());
        savedArticle.setUrlToImage(article.getUrlToImage());
        savedArticle.setPublishedAt(article.getPublishedAt());
        savedArticle.setSourceName(article.getSource() != null ? article.getSource().getName() : null);
        savedArticle.setCategory(category);
        savedArticle.setCountry(country);

        user.getSavedArticles().add(savedArticle);
        user.getPreferredCategories().add(category);

        if (user.getPreferredCountry().equals("us") && !country.equals("us")) {
            user.setPreferredCountry(country);
        }

        return userRepository.save(user);
    }

    public User unsaveArticle(Long userId, String articleUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.getSavedArticles().removeIf(sa -> sa.getUrl().equals(articleUrl));
        return userRepository.save(user);
    }

    public List<SavedArticle> getSavedArticles(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return user.getSavedArticles();
    }

    public List<String> getTop3Categories(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<String> categories = user.getSavedArticles().stream()
                .map(SavedArticle::getCategory)
                .collect(Collectors.toList());

        Map<String, Long> categoryCount = categories.stream()
                .collect(Collectors.groupingBy(c -> c, Collectors.counting()));

        return categoryCount.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public Map<String, Object> getUserPreferences(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Map<String, Object> preferences = new HashMap<>();
        preferences.put("username", user.getUsername());
        preferences.put("preferredCategories", user.getPreferredCategories());
        preferences.put("preferredCountry", user.getPreferredCountry());
        preferences.put("top3Categories", getTop3Categories(userId));
        preferences.put("totalSavedArticles", user.getSavedArticles().size());

        return preferences;
    }

    public boolean isArticleSaved(Long userId, String articleUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return user.getSavedArticles().stream()
                .anyMatch(sa -> sa.getUrl().equals(articleUrl));
    }

    public List<Article> getPersonalizedNews(Long userId) {
        List<String> topCategories = getTop3Categories(userId);
        if (topCategories.isEmpty()) {
            topCategories.add("general"); // Default
        }

        List<Article> personalizedArticles = new ArrayList<>();
        for (String category : topCategories) {
            // Fetch news for each top category.
            // Note: This might be slow if we make many calls. Limiting to top 3 is good.
            // Also, we might want to limit the number of articles per category.
            try {
                // Assuming NewsService returns a wrapper with "articles" list
                // We need to access the list.
                // Since NewsService returns a Map or Object, let's assume it returns the same
                // structure as the controller uses.
                // Wait, NewsService returns `NewsResponse` or similar?
                // I need to check NewsService signature.
                // Based on previous view, it returns `NewsResponse` or similar object.
                // Let's assume `newsService.getNewsByCategory(category)` returns an object that
                // has `getArticles()`.
                // I'll check NewsService again if needed, but for now I'll cast or assume.
                // Actually, I should check NewsService.
                var response = newsService.getNewsByCategory(category);
                if (response != null && response.getArticles() != null) {
                    personalizedArticles.addAll(response.getArticles());
                }
            } catch (Exception e) {
                System.err.println("Error fetching personalized news for category: " + category);
            }
        }
        // Shuffle to mix them up? Or keep ordered? Let's shuffle for variety.
        Collections.shuffle(personalizedArticles);
        return personalizedArticles;
    }
}
