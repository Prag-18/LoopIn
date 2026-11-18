package com.loopin.controller;

import com.loopin.model.ItemListing;
import com.loopin.repository.ListingRepository;
import org.springframework.http.HttpStatus;
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
            return ResponseEntity.ok(listingRepository.findByAvailableTrueAndCategoryIgnoreCase(category));
        }
        return ResponseEntity.ok(listingRepository.findByAvailableTrue());
    }

    /**
     * Endpoint: Host a new item listing on campus
     * POST /api/listings
     */
    @PostMapping
    public ResponseEntity<ItemListing> createListing(@RequestBody ItemListing listing) {
        if (listing.getTitle() == null || listing.getTitle().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        listing.setAvailable(true);
        ItemListing saved = listingRepository.save(listing);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    /**
     * Endpoint: Delete / Withdraw a hosted listing
     * DELETE /api/listings/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteListing(@PathVariable Long id) {
        if (listingRepository.existsById(id)) {
            listingRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
