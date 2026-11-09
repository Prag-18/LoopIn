package com.loopin.repository;

import com.loopin.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository for User entity.
 * Demonstrates derived query methods and custom JPQL query.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Derived query method 1: Find user by unique email
    Optional<User> findByEmail(String email);

    // Derived query method 2: Check existence by email
    boolean existsByEmail(String email);

    // Custom JPQL Query: Retrieve users by department
    @Query("SELECT u FROM User u WHERE LOWER(u.department) = LOWER(:department)")
    List<User> findByDepartmentIgnoreCase(@Param("department") String department);
}
