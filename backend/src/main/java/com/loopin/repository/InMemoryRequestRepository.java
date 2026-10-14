package com.loopin.repository;

import com.loopin.model.ItemRequest;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Data Access Layer implementation of RequestRepository.
 * Manages in-memory storage of ItemRequests using thread-safe data structures.
 */
@Repository
public class InMemoryRequestRepository implements RequestRepository {

    private final Map<Long, ItemRequest> database = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    public InMemoryRequestRepository() {
        // Pre-populate with initial campus requests for demonstration
        LocalDate today = LocalDate.now();
        save(new ItemRequest(null, "TI-84 Graphing Calculator", "Academics", "Alex Morgan",
                "alex.m@campus.edu", today.plusDays(1), today.plusDays(5), 1, "OPEN",
                "Needed for Calculus III midterm exam"));

        save(new ItemRequest(null, "Cotton White Lab Coat", "Academics", "Priya Sharma",
                "priya.s@campus.ac.in", today.plusDays(2), today.plusDays(4), 2, "OPEN",
                "Organic Chemistry Lab on Thursday"));

        save(new ItemRequest(null, "Sony Alpha Camera with Tripod", "Electronics", "David Kim",
                "david.k@campus.edu", today.plusDays(3), today.plusDays(6), 3, "OPEN",
                "Weekend film festival project"));
    }

    @Override
    public ItemRequest save(ItemRequest request) {
        if (request.getId() == null) {
            request.setId(idCounter.getAndIncrement());
        }
        database.put(request.getId(), request);
        return request;
    }

    @Override
    public Optional<ItemRequest> findById(Long id) {
        return Optional.ofNullable(database.get(id));
    }

    @Override
    public List<ItemRequest> findAll() {
        return new ArrayList<>(database.values());
    }

    @Override
    public List<ItemRequest> findByStatus(String status) {
        return database.values().stream()
                .filter(req -> req.getStatus() != null && req.getStatus().equalsIgnoreCase(status))
                .collect(Collectors.toList());
    }

    @Override
    public boolean deleteById(Long id) {
        return database.remove(id) != null;
    }
}
