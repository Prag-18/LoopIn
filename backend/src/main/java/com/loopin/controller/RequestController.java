package com.loopin.controller;

import com.loopin.model.ItemListing;
import com.loopin.model.ItemRequest;
import com.loopin.service.RequestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Presentation Layer: REST Controller for Item Requests.
 * Exposes endpoints for client communication and delegates all business logic
 * to the RequestService.
 */
@RestController
@RequestMapping("/api/requests")
@CrossOrigin(origins = "*") // Allows communication with frontend Vite / React app
public class RequestController {

    private final RequestService requestService;

    // Constructor injection of the Service layer
    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    /**
     * Endpoint 1: Retrieve all campus item requests
     * GET /api/requests
     */
    @GetMapping
    public ResponseEntity<List<ItemRequest>> getAllRequests() {
        List<ItemRequest> requests = requestService.getAllRequests();
        return ResponseEntity.ok(requests);
    }

    /**
     * Endpoint 2: Retrieve a single request by its unique ID
     * GET /api/requests/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ItemRequest> getRequestById(@PathVariable Long id) {
        ItemRequest request = requestService.getRequestById(id);
        return ResponseEntity.ok(request);
    }

    /**
     * Endpoint 3: Create a new borrow request
     * POST /api/requests
     */
    @PostMapping
    public ResponseEntity<ItemRequest> createRequest(@RequestBody ItemRequest request) {
        ItemRequest created = requestService.createRequest(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /**
     * Endpoint 4: Update request status (e.g. FULFILLED or CANCELLED)
     * PUT /api/requests/{id}/status?status=FULFILLED
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<ItemRequest> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        ItemRequest updated = requestService.updateRequestStatus(id, status);
        return ResponseEntity.ok(updated);
    }

    /**
     * Endpoint 5: Find matching community listings for a request
     * GET /api/requests/{id}/matches
     */
    @GetMapping("/{id}/matches")
    public ResponseEntity<List<ItemListing>> getMatches(@PathVariable Long id) {
        List<ItemListing> matches = requestService.findMatchingListings(id);
        return ResponseEntity.ok(matches);
    }
}
