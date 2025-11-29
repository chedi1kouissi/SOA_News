package com.example.SOA_News.repository;

import com.example.SOA_News.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by username
     */
    Optional<User> findByUsername(String username);

    /**
     * Check if username already exists
     */
    boolean existsByUsername(String username);

    /**
     * Find users by preferred country
     */
    List<User> findByPreferredCountry(String country);

    /**
     * Find users who have a specific category in their preferences
     * This queries the @ElementCollection
     */
    @Query("SELECT u FROM User u JOIN u.preferredCategories c WHERE c = :category")
    List<User> findByPreferredCategory(@Param("category") String category);

    /**
     * Find users who have saved a specific article URL
     */
    @Query("SELECT u FROM User u JOIN u.savedArticles a WHERE a.url = :articleUrl")
    List<User> findBySavedArticleUrl(@Param("articleUrl") String articleUrl);

    /**
     * Count users by preferred country
     */
    long countByPreferredCountry(String country);
}
