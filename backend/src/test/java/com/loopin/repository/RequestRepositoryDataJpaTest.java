package com.loopin.repository;

import com.loopin.model.ItemRequest;
import com.loopin.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Data Layer Integration Test using Spring Boot and H2 in-memory database.
 * Tests Spring Data JPA entity mapping, relationships, derived queries, and custom JPQL.
 */
@SpringBootTest
@Transactional
class RequestRepositoryDataJpaTest {

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("JPA Test 1: Save User and ItemRequest with @OneToMany / @ManyToOne relationship")
    void shouldPersistUserAndAssociatedItemRequest() {
        // Given a User parent entity
        User student = new User(null, "Maya Patel", "maya.p@campus.edu", "Physics");
        User savedUser = userRepository.save(student);

        assertThat(savedUser.getId()).isNotNull();

        // Given an ItemRequest attempt linked to the User
        ItemRequest request = new ItemRequest(
                null, "Vernier Caliper", "Lab Gear",
                savedUser.getName(), savedUser.getEmail(),
                LocalDate.now().plusDays(1), LocalDate.now().plusDays(3),
                1, "OPEN", "Mechanics Lab Experiment"
        );
        request.setUser(savedUser);
        ItemRequest savedRequest = requestRepository.save(request);

        // Then verify database persistence
        assertThat(savedRequest.getId()).isNotNull();
        assertThat(savedRequest.getUser().getId()).isEqualTo(savedUser.getId());
        assertThat(savedRequest.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("JPA Test 2: Derived query method findByRequesterEmailOrderByCreatedAtDesc")
    void shouldFindRequestsByEmailOrderedByDate() {
        User student = userRepository.save(new User(null, "Leo Chen", "leo.c@campus.edu", "Design"));

        ItemRequest req1 = new ItemRequest(
                null, "Wacom Tablet", "Electronics",
                student.getName(), student.getEmail(),
                LocalDate.now().plusDays(1), LocalDate.now().plusDays(4),
                2, "OPEN", "Digital art class"
        );
        req1.setUser(student);
        requestRepository.save(req1);

        // Execute derived query
        List<ItemRequest> results = requestRepository.findByRequesterEmailOrderByCreatedAtDesc("leo.c@campus.edu");

        assertThat(results).isNotEmpty();
        assertThat(results.get(0).getItemName()).isEqualTo("Wacom Tablet");
    }

    @Test
    @DisplayName("JPA Test 3: Custom JPQL Query countByStatus")
    void shouldCountRequestsByStatusUsingCustomQuery() {
        User student = userRepository.save(new User(null, "Sarah Jenkins", "sarah.j@campus.edu", "Biology"));

        ItemRequest openReq = new ItemRequest(
                null, "Dissection Kit", "Academics",
                student.getName(), student.getEmail(),
                LocalDate.now().plusDays(2), LocalDate.now().plusDays(5),
                1, "OPEN", "Anatomy lab"
        );
        openReq.setUser(student);
        requestRepository.save(openReq);

        // Custom JPQL query execution
        long openCount = requestRepository.countByStatus("OPEN");
        assertThat(openCount).isGreaterThanOrEqualTo(1);
    }
}
