package com.example.SOA_News.controller;

import com.example.SOA_News.model.Article;
import com.example.SOA_News.model.SavedArticle;
import com.example.SOA_News.model.User;
import com.example.SOA_News.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            User createdUser = userService.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        if (userService.authenticateUser(username, password)) {
            User user = userService.getUserByUsername(username).orElseThrow();
            return ResponseEntity.ok(Map.of(
                    "message", "Login successful",
                    "userId", user.getId(),
                    "username", user.getUsername()));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid username or password"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/allUsers")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}/preferences")
    public ResponseEntity<?> getUserPreferences(@PathVariable Long id) {
        try {
            Map<String, Object> preferences = userService.getUserPreferences(id);
            return ResponseEntity.ok(preferences);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/saved-articles")
    public ResponseEntity<?> saveArticle(@PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        try {
            // Map request to Article object
            // This is a bit manual, but safer than assuming automatic mapping works
            // perfectly with nested objects in a Map
            // Ideally we'd use a DTO class for the request body, but let's stick to Map for
            // flexibility or use ObjectMapper
            // Actually, we can just accept a custom DTO or map manually.

            String category = (String) request.get("category");
            String country = (String) request.get("country");

            // Extract article fields
            Map<String, Object> articleMap = (Map<String, Object>) request.get("article");
            Article article = new Article();
            article.setUrl((String) articleMap.get("url"));
            article.setTitle((String) articleMap.get("title"));
            article.setDescription((String) articleMap.get("description"));
            article.setUrlToImage((String) articleMap.get("urlToImage"));
            article.setPublishedAt((String) articleMap.get("publishedAt"));

            // Source
            if (articleMap.get("source") != null) {
                Map<String, String> sourceMap = (Map<String, String>) articleMap.get("source");
                Article.Source source = new Article.Source();
                source.setName(sourceMap.get("name"));
                article.setSource(source);
            }

            User user = userService.saveArticle(id, article, category, country);
            return ResponseEntity.ok(Map.of(
                    "message", "Article saved successfully",
                    "user", user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}/saved-articles")
    public ResponseEntity<?> unsaveArticle(@PathVariable Long id,
            @RequestBody Map<String, String> request) {
        try {
            String articleUrl = request.get("articleUrl");
            userService.unsaveArticle(id, articleUrl);
            return ResponseEntity.ok(Map.of("message", "Article removed"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}/saved-articles")
    public ResponseEntity<?> getSavedArticles(@PathVariable Long id) {
        try {
            List<SavedArticle> articles = userService.getSavedArticles(id);
            return ResponseEntity.ok(articles);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}/saved-articles/check")
    public ResponseEntity<?> checkArticleSaved(@PathVariable Long id,
            @RequestParam String articleUrl) {
        try {
            boolean isSaved = userService.isArticleSaved(id, articleUrl);
            return ResponseEntity.ok(Map.of("isSaved", isSaved));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}/top-categories")
    public ResponseEntity<?> getTop3Categories(@PathVariable Long id) {
        try {
            List<String> top3 = userService.getTop3Categories(id);
            return ResponseEntity.ok(Map.of("top3Categories", top3));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}/personalized-news")
    public ResponseEntity<?> getPersonalizedNews(@PathVariable Long id) {
        try {
            List<Article> articles = userService.getPersonalizedNews(id);
            return ResponseEntity.ok(Map.of("articles", articles));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
