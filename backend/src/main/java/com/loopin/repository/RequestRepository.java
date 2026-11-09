package com.loopin.repository;

import com.loopin.model.ItemRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA Repository for ItemRequest entity.
 * Provides derived query methods and custom JPQL queries.
 */
@Repository
public interface RequestRepository extends JpaRepository<ItemRequest, Long> {

    // Derived Query Method 1: Find by status (e.g. OPEN, FULFILLED)
    List<ItemRequest> findByStatus(String status);

    // Derived Query Method 2: Find requests by email ordered chronologically descending
    List<ItemRequest> findByRequesterEmailOrderByCreatedAtDesc(String requesterEmail);

    // Derived Query Method 3: Find requests by user ID ordered chronologically descending
    List<ItemRequest> findByUserIdOrderByCreatedAtDesc(Long userId);

    // Derived Query Method 4: Top 5 recent requests
    List<ItemRequest> findTop5ByOrderByCreatedAtDesc();

    // Custom JPQL Query 1: Count total requests by specific status
    @Query("SELECT COUNT(r) FROM ItemRequest r WHERE LOWER(r.status) = LOWER(:status)")
    long countByStatus(@Param("status") String status);

    // Custom JPQL Query 2: Count requests by user id
    @Query("SELECT COUNT(r) FROM ItemRequest r WHERE r.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);
}
