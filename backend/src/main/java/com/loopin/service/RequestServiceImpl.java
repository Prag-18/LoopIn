package com.loopin.service;

import com.loopin.model.ItemListing;
import com.loopin.model.ItemRequest;
import com.loopin.repository.ListingRepository;
import com.loopin.repository.RequestRepository;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Business Layer Implementation of RequestService.
 * Enforces business logic and validation rules before interacting with the Data Access Layer.
 */
@Service
public class RequestServiceImpl implements RequestService {

    private static final int MAX_BORROW_DAYS = 30;

    private final RequestRepository requestRepository;
    private final ListingRepository listingRepository;

    // Constructor-based dependency injection
    public RequestServiceImpl(RequestRepository requestRepository, ListingRepository listingRepository) {
        this.requestRepository = requestRepository;
        this.listingRepository = listingRepository;
    }

    @Override
    public List<ItemRequest> getAllRequests() {
        return requestRepository.findAll();
    }

    @Override
    public ItemRequest getRequestById(Long id) {
        return requestRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Item request not found with ID: " + id));
    }

    @Override
    public ItemRequest createRequest(ItemRequest request) {
        // Enforce Business Rule 1: Dates and Borrow Duration Validation
        validateBorrowDuration(request);

        // Enforce Business Rule 2: Trust Radius and Requester Validation
        validateRequesterAndTrustRadius(request);

        // Set default status if not provided
        if (request.getStatus() == null || request.getStatus().isBlank()) {
            request.setStatus("OPEN");
        }

        // Delegate to Data Access Layer
        return requestRepository.save(request);
    }

    @Override
    public ItemRequest updateRequestStatus(Long id, String newStatus) {
        ItemRequest existing = getRequestById(id);

        if (newStatus == null || newStatus.isBlank()) {
            throw new IllegalArgumentException("Target status cannot be empty.");
        }

        String normalizedStatus = newStatus.trim().toUpperCase();

        // Enforce Business Rule 3: State Transition Rule
        if (!"OPEN".equalsIgnoreCase(existing.getStatus())) {
            throw new IllegalStateException("Cannot change status of a request that is already " + existing.getStatus());
        }

        if (!normalizedStatus.equals("FULFILLED") && !normalizedStatus.equals("CANCELLED") && !normalizedStatus.equals("OPEN")) {
            throw new IllegalArgumentException("Invalid status transition: " + newStatus + ". Allowed: OPEN, FULFILLED, CANCELLED.");
        }

        existing.setStatus(normalizedStatus);
        return requestRepository.save(existing);
    }

    @Override
    public List<ItemListing> findMatchingListings(Long requestId) {
        ItemRequest req = getRequestById(requestId);
        String category = req.getCategory();
        int trustRadius = req.getTrustRadius();

        // Cross-domain business logic: Find available listings that fit within the requester's trust degree
        return listingRepository.findAvailableByCategory(category).stream()
                .filter(listing -> listing.getMinTrustDegree() >= trustRadius)
                .collect(Collectors.toList());
    }

    /**
     * BUSINESS RULE 1:
     * Validates that start date and end date are present, end date does not precede start date,
     * and the total borrowing duration does not exceed the campus maximum limit (30 days).
     */
    private void validateBorrowDuration(ItemRequest request) {
        if (request.getStartDate() == null || request.getEndDate() == null) {
            throw new IllegalArgumentException("Start date and end date are mandatory for creating an item request.");
        }

        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new IllegalArgumentException("End date cannot be earlier than start date.");
        }

        long durationDays = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()) + 1;
        if (durationDays > MAX_BORROW_DAYS) {
            throw new IllegalArgumentException("Borrow duration cannot exceed " + MAX_BORROW_DAYS 
                    + " days. Requested duration: " + durationDays + " days.");
        }
    }

    /**
     * BUSINESS RULE 2:
     * Validates item details, requester campus eligibility, and trust radius constraints.
     * Trust radius must be 1 (1st degree), 2 (2nd degree), or 3 (campus-wide).
     * Requester email must belong to an academic campus domain.
     */
    private void validateRequesterAndTrustRadius(ItemRequest request) {
        if (request.getItemName() == null || request.getItemName().trim().isEmpty()) {
            throw new IllegalArgumentException("Item name cannot be blank.");
        }

        if (request.getTrustRadius() < 1 || request.getTrustRadius() > 3) {
            throw new IllegalArgumentException("Trust radius must be between 1 and 3 (1=Friends, 2=Mutuals, 3=Campus-wide). Given: " 
                    + request.getTrustRadius());
        }

        if (request.getRequesterEmail() == null || !request.getRequesterEmail().contains("@")) {
            throw new IllegalArgumentException("A valid campus email is required to request items.");
        }

        String email = request.getRequesterEmail().toLowerCase();
        boolean isCampusEmail = email.endsWith(".edu") || email.endsWith(".ac.in") || email.contains("campus");
        if (!isCampusEmail) {
            throw new IllegalArgumentException("Requester email must be a valid institutional/campus email (.edu or .ac.in).");
        }
    }
}
