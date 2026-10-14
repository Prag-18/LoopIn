package com.loopin.controller;

import com.loopin.model.ItemListing;
import com.loopin.repository.ListingRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Presentation Layer: REST Controller for Available Community Listings.
 */
@RestController
@RequestMapping("/api/listings")
@CrossOrigin(origins = "*")
public class ListingController {

    private final ListingRepository listingRepository;

    public ListingController(ListingRepository listingRepository) {
        this.listingRepository = listingRepository;
    }

    /**
     * Endpoint: Retrieve available gear listings
     * GET /api/listings
     */
    @GetMapping
    public ResponseEntity<List<ItemListing>> getAllListings(
            @RequestParam(required = false) String category) {
        if (category != null && !category.isBlank()) {
            return ResponseEntity.ok(listingRepository.findAvailableByCategory(category));
        }
        return ResponseEntity.ok(listingRepository.findAll());
    }
}
