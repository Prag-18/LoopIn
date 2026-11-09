package com.loopin.service;

import com.loopin.client.TrustServiceClient;
import com.loopin.client.TrustVerificationResponse;
import com.loopin.model.ItemRequest;
import com.loopin.model.User;
import com.loopin.repository.ListingRepository;
import com.loopin.repository.RequestRepository;
import com.loopin.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Automated Unit Tests for RequestService business rules using JUnit 5 and Mockito.
 */
class RequestServiceTest {

    private RequestRepository requestRepository;
    private UserRepository userRepository;
    private ListingRepository listingRepository;
    private TrustServiceClient trustServiceClient;
    private RequestService requestService;

    @BeforeEach
    void setUp() {
        requestRepository = Mockito.mock(RequestRepository.class);
        userRepository = Mockito.mock(UserRepository.class);
        listingRepository = Mockito.mock(ListingRepository.class);
        trustServiceClient = Mockito.mock(TrustServiceClient.class);

        requestService = new RequestServiceImpl(
                requestRepository,
                userRepository,
                listingRepository,
                trustServiceClient
        );
    }

    @Test
    @DisplayName("Test 1: Valid borrow request creation succeeds and provisions User (Happy Path)")
    void testCreateRequest_Success_ValidInput() {
        LocalDate today = LocalDate.now();
        ItemRequest newRequest = new ItemRequest(
                null, "Arduino Uno Kit", "Electronics", "Sam Wilson",
                "sam.w@campus.edu", today.plusDays(1), today.plusDays(7),
                2, null, "Hackathon"
        );

        User mockUser = new User(10L, "Sam Wilson", "sam.w@campus.edu", "Campus Community");
        when(userRepository.findByEmail("sam.w@campus.edu")).thenReturn(Optional.of(mockUser));
        when(trustServiceClient.verifyTrust(any(), any(Integer.class), any()))
                .thenReturn(new TrustVerificationResponse("sam.w@campus.edu", true, 80, "VERIFIED_CAMPUS_MEMBER", 3));

        ItemRequest savedMock = new ItemRequest(
                1L, "Arduino Uno Kit", "Electronics", "Sam Wilson",
                "sam.w@campus.edu", today.plusDays(1), today.plusDays(7),
                2, "OPEN", "Hackathon"
        );
        savedMock.setUser(mockUser);
        savedMock.setTrustVerificationStatus("VERIFIED_CAMPUS_MEMBER");
        when(requestRepository.save(any(ItemRequest.class))).thenReturn(savedMock);

        ItemRequest result = requestService.createRequest(newRequest);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Arduino Uno Kit", result.getItemName());
        assertEquals("OPEN", result.getStatus());
        assertEquals("VERIFIED_CAMPUS_MEMBER", result.getTrustVerificationStatus());
    }

    @Test
    @DisplayName("Test 2: Fails when End Date is before Start Date (Business Rule 1)")
    void testCreateRequest_Fails_WhenEndDateBeforeStartDate() {
        LocalDate today = LocalDate.now();
        ItemRequest invalid = new ItemRequest(
                null, "Microbiology Book", "Academics", "Lisa Ray",
                "lisa.r@campus.ac.in", today.plusDays(5), today.plusDays(1), 1, "OPEN", "Exam"
        );

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> requestService.createRequest(invalid)
        );
        assertTrue(ex.getMessage().contains("End date cannot be earlier than start date"));
    }

    @Test
    @DisplayName("Test 3: Fails when borrow duration exceeds 30-day limit (Business Rule 1)")
    void testCreateRequest_Fails_WhenDurationExceeds30Days() {
        LocalDate today = LocalDate.now();
        ItemRequest longDuration = new ItemRequest(
                null, "DSLR Lens", "Electronics", "Lisa Ray",
                "lisa.r@campus.edu", today, today.plusDays(40), 3, "OPEN", "Project"
        );

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> requestService.createRequest(longDuration)
        );
        assertTrue(ex.getMessage().contains("Borrow duration cannot exceed 30 days"));
    }

    @Test
    @DisplayName("Test 4: Fails when trust radius is out of range 1-3 (Business Rule 2)")
    void testCreateRequest_Fails_WhenTrustRadiusInvalid() {
        LocalDate today = LocalDate.now();
        ItemRequest invalidRadius = new ItemRequest(
                null, "Calculator", "Academics", "Sam",
                "sam.w@campus.edu", today.plusDays(1), today.plusDays(3), 5, "OPEN", "Prep"
        );

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> requestService.createRequest(invalidRadius)
        );
        assertTrue(ex.getMessage().contains("Trust radius must be between 1 and 3"));
    }

    @Test
    @DisplayName("Test 5: Fails when requester provides non-campus email (Business Rule 2)")
    void testCreateRequest_Fails_WhenNonCampusEmail() {
        LocalDate today = LocalDate.now();
        ItemRequest externalEmail = new ItemRequest(
                null, "Lab Goggles", "Academics", "External User",
                "outsider@gmail.com", today.plusDays(1), today.plusDays(3), 1, "OPEN", "Lab"
        );

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> requestService.createRequest(externalEmail)
        );
        assertTrue(ex.getMessage().contains("Requester email must be a valid institutional/campus email"));
    }

    @Test
    @DisplayName("Test 6: Fails state transition when request is already FULFILLED (Business Rule 3)")
    void testUpdateRequestStatus_Fails_WhenAlreadyFulfilled() {
        ItemRequest existing = new ItemRequest();
        existing.setId(1L);
        existing.setStatus("FULFILLED");

        when(requestRepository.findById(1L)).thenReturn(Optional.of(existing));

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> requestService.updateRequestStatus(1L, "CANCELLED")
        );
        assertTrue(ex.getMessage().contains("Cannot change status of a request that is already FULFILLED"));
    }
}
