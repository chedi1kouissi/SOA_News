package com.example.SOA_News.controller;

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
                "username", user.getUsername()
            ));
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

    @GetMapping()
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    
    // Get user preferences with top 3 categories
    @GetMapping("/{id}/preferences")
    public ResponseEntity<?> getUserPreferences(@PathVariable Long id) {
        try {
            Map<String, Object> preferences = userService.getUserPreferences(id);
            return ResponseEntity.ok(preferences);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // Save article with category and country
    @PostMapping("/{id}/saved-articles")
    public ResponseEntity<?> saveArticle(@PathVariable Long id, 
                                        @RequestBody Map<String, String> request) {
        try {
            String articleUrl = request.get("articleUrl");
            String category = request.get("category");
            String country = request.get("country");
            
            User user = userService.saveArticle(id, articleUrl, category, country);
            return ResponseEntity.ok(Map.of(
                "message", "Article saved successfully",
                "user", user
            ));
        } catch (RuntimeException e) {
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
            List<Map<String, String>> articles = userService.getSavedArticles(id);
            return ResponseEntity.ok(articles);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // Check if article is saved
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
    
    // Get top 3 categories
    @GetMapping("/{id}/top-categories")
    public ResponseEntity<?> getTop3Categories(@PathVariable Long id) {
        try {
            List<String> top3 = userService.getTop3Categories(id);
            return ResponseEntity.ok(Map.of("top3Categories", top3));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
