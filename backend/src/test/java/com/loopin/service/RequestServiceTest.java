package com.loopin.service;

import com.loopin.model.ItemRequest;
import com.loopin.repository.InMemoryListingRepository;
import com.loopin.repository.InMemoryRequestRepository;
import com.loopin.repository.ListingRepository;
import com.loopin.repository.RequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Automated Unit Tests for RequestService business rules using JUnit 5.
 */
class RequestServiceTest {

    private RequestRepository requestRepository;
    private ListingRepository listingRepository;
    private RequestService requestService;

    @BeforeEach
    void setUp() {
        requestRepository = new InMemoryRequestRepository();
        listingRepository = new InMemoryListingRepository();
        requestService = new RequestServiceImpl(requestRepository, listingRepository);
    }

    @Test
    @DisplayName("Test 1: Valid borrow request creation succeeds (Happy Path)")
    void testCreateRequest_Success_ValidInput() {
        LocalDate today = LocalDate.now();
        ItemRequest newRequest = new ItemRequest(
                null,
                "Arduino Uno Starter Kit",
                "Electronics",
                "Sam Wilson",
                "sam.w@campus.edu",
                today.plusDays(1),
                today.plusDays(7),
                2,
                null,
                "Robotics Club hackathon"
        );

        ItemRequest result = requestService.createRequest(newRequest);

        assertNotNull(result.getId(), "Generated ID should not be null");
        assertEquals("Arduino Uno Starter Kit", result.getItemName());
        assertEquals("OPEN", result.getStatus(), "Default status should be OPEN");
        assertEquals("sam.w@campus.edu", result.getRequesterEmail());
    }

    @Test
    @DisplayName("Test 2: Fails when End Date is before Start Date (Business Rule 1)")
    void testCreateRequest_Fails_WhenEndDateBeforeStartDate() {
        LocalDate today = LocalDate.now();
        ItemRequest invalidDatesRequest = new ItemRequest(
                null,
                "Microbiology Textbook",
                "Academics",
                "Lisa Ray",
                "lisa.r@campus.ac.in",
                today.plusDays(5),
                today.plusDays(1), // End date is BEFORE start date
                1,
                "OPEN",
                "Studying for finals"
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> requestService.createRequest(invalidDatesRequest),
                "Expected IllegalArgumentException for end date preceding start date"
        );

        assertTrue(exception.getMessage().contains("End date cannot be earlier than start date"));
    }

    @Test
    @DisplayName("Test 3: Fails when borrow duration exceeds 30-day limit (Business Rule 1)")
    void testCreateRequest_Fails_WhenDurationExceeds30Days() {
        LocalDate today = LocalDate.now();
        ItemRequest longDurationRequest = new ItemRequest(
                null,
                "DSLR Telephoto Lens",
                "Electronics",
                "Lisa Ray",
                "lisa.r@campus.edu",
                today,
                today.plusDays(40), // 41 days duration > 30 max
                3,
                "OPEN",
                "Semester film project"
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> requestService.createRequest(longDurationRequest),
                "Expected IllegalArgumentException for borrowing period > 30 days"
        );

        assertTrue(exception.getMessage().contains("Borrow duration cannot exceed 30 days"));
    }

    @Test
    @DisplayName("Test 4: Fails when trust radius is out of range 1-3 (Business Rule 2)")
    void testCreateRequest_Fails_WhenTrustRadiusInvalid() {
        LocalDate today = LocalDate.now();
        ItemRequest invalidRadiusRequest = new ItemRequest(
                null,
                "Graphing Calculator",
                "Academics",
                "Sam Wilson",
                "sam.w@campus.edu",
                today.plusDays(1),
                today.plusDays(3),
                5, // Invalid radius (must be 1, 2, or 3)
                "OPEN",
                "Exam prep"
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> requestService.createRequest(invalidRadiusRequest),
                "Expected IllegalArgumentException for invalid trust radius"
        );

        assertTrue(exception.getMessage().contains("Trust radius must be between 1 and 3"));
    }

    @Test
    @DisplayName("Test 5: Fails when requester provides non-campus email (Business Rule 2)")
    void testCreateRequest_Fails_WhenNonCampusEmail() {
        LocalDate today = LocalDate.now();
        ItemRequest externalEmailRequest = new ItemRequest(
                null,
                "Lab Goggles",
                "Academics",
                "External User",
                "outsider@gmail.com", // Non-institutional email
                today.plusDays(1),
                today.plusDays(3),
                1,
                "OPEN",
                "Chemistry Lab"
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> requestService.createRequest(externalEmailRequest),
                "Expected IllegalArgumentException for non-campus email"
        );

        assertTrue(exception.getMessage().contains("Requester email must be a valid institutional/campus email"));
    }

    @Test
    @DisplayName("Test 6: Fails state transition when request is already FULFILLED (Business Rule 3)")
    void testUpdateRequestStatus_Fails_WhenAlreadyFulfilled() {
        // ID 1 is pre-populated in OPEN state
        requestService.updateRequestStatus(1L, "FULFILLED");

        // Attempting to transition already FULFILLED request
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> requestService.updateRequestStatus(1L, "CANCELLED"),
                "Expected IllegalStateException when modifying already fulfilled request"
        );

        assertTrue(exception.getMessage().contains("Cannot change status of a request that is already FULFILLED"));
    }
}
