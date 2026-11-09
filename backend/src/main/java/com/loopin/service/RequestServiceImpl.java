package com.loopin.service;

import com.loopin.client.TrustServiceClient;
import com.loopin.client.TrustVerificationResponse;
import com.loopin.model.ItemListing;
import com.loopin.model.ItemRequest;
import com.loopin.model.User;
import com.loopin.repository.ListingRepository;
import com.loopin.repository.RequestRepository;
import com.loopin.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Business Layer Implementation of RequestService.
 * Enforces business logic and validation rules, manages User/ItemRequest JPA persistence,
 * and coordinates inter-service verification with Service 2.
 */
@Service
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {

    private static final int MAX_BORROW_DAYS = 30;

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ListingRepository listingRepository;
    private final TrustServiceClient trustServiceClient;

    public RequestServiceImpl(RequestRepository requestRepository,
                              UserRepository userRepository,
                              ListingRepository listingRepository,
                              TrustServiceClient trustServiceClient) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.listingRepository = listingRepository;
        this.trustServiceClient = trustServiceClient;
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

    /**
     * Storing Users and Attempts:
     * Validates business rules, creates or reuses the User parent entity,
     * calls Microservice 2 for trust verification, and saves the ItemRequest attempt.
     */
    @Override
    @Transactional
    public ItemRequest createRequest(ItemRequest request) {
        // Enforce Business Rule 1: Dates and Borrow Duration Validation
        validateBorrowDuration(request);

        // Enforce Business Rule 2: Trust Radius and Requester Validation
        validateRequesterAndTrustRadius(request);

        // Find existing user or create a new User record
        String email = request.getRequesterEmail().trim().toLowerCase();
        String name = (request.getRequesterName() != null && !request.getRequesterName().isBlank())
                ? request.getRequesterName().trim()
                : email.split("@")[0];

        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setName(name);
            newUser.setDepartment("Campus Community");
            return userRepository.save(newUser);
        });

        // Link parent User to child ItemRequest attempt
        request.setUser(user);
        request.setRequesterEmail(email);
        request.setRequesterName(user.getName());

        if (request.getStatus() == null || request.getStatus().isBlank()) {
            request.setStatus("OPEN");
        }

        // Inter-Service Communication: Call Microservice 2 (Trust Service)
        try {
            TrustVerificationResponse trustResult = trustServiceClient.verifyTrust(
                    request.getRequesterEmail(),
                    request.getTrustRadius(),
                    request.getItemName()
            );
            request.setTrustVerificationStatus(trustResult.getStatusMessage());
        } catch (Exception ex) {
            request.setTrustVerificationStatus("FALLBACK_VERIFICATION");
        }

        return requestRepository.save(request);
    }

    @Override
    @Transactional
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

        return listingRepository.findByAvailableTrueAndCategoryIgnoreCase(category).stream()
                .filter(listing -> listing.getMinTrustDegree() >= trustRadius)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequest> getRequestsByUser(Long userId) {
        return requestRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    public List<ItemRequest> getRequestsByUserEmail(String email) {
        return requestRepository.findByRequesterEmailOrderByCreatedAtDesc(email);
    }

    @Override
    public Map<String, Object> getBorrowingStatistics() {
        Map<String, Object> stats = new LinkedHashMap<>();
        long totalRequests = requestRepository.count();
        long openRequests = requestRepository.countByStatus("OPEN");
        long fulfilledRequests = requestRepository.countByStatus("FULFILLED");
        long cancelledRequests = requestRepository.countByStatus("CANCELLED");
        long totalUsers = userRepository.count();

        stats.put("totalBorrowAttempts", totalRequests);
        stats.put("openRequests", openRequests);
        stats.put("fulfilledRequests", fulfilledRequests);
        stats.put("cancelledRequests", cancelledRequests);
        stats.put("registeredUsers", totalUsers);
        stats.put("successRatePercent", totalRequests > 0 ? (fulfilledRequests * 100.0 / totalRequests) : 0.0);

        return stats;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found with ID: " + userId));
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
