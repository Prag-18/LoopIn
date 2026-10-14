package com.loopin.repository;

import com.loopin.model.ItemListing;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Data Access Layer implementation of ListingRepository.
 * Stores available campus gear listings in-memory.
 */
@Repository
public class InMemoryListingRepository implements ListingRepository {

    private final Map<Long, ItemListing> database = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    public InMemoryListingRepository() {
        // Pre-populate with sample available listings
        save(new ItemListing(null, "Texas Instruments TI-84 Plus CE", "Academics", "Emma Watson",
                "emma.w@campus.edu", 1, true, "EXCELLENT"));

        save(new ItemListing(null, "White Lab Coat - 100% Cotton (Unisex M)", "Academics", "Rahul Patel",
                "rahul.p@campus.ac.in", 2, true, "GOOD"));

        save(new ItemListing(null, "Sony A6400 Mirrorless Camera & 50mm Lens", "Electronics", "Sophia Chen",
                "sophia.c@campus.edu", 3, true, "EXCELLENT"));

        save(new ItemListing(null, "Anker USB-C to HDMI 4K Adapter", "Electronics", "Liam Davis",
                "liam.d@campus.edu", 2, true, "GOOD"));
    }

    @Override
    public List<ItemListing> findAll() {
        return new ArrayList<>(database.values());
    }

    @Override
    public Optional<ItemListing> findById(Long id) {
        return Optional.ofNullable(database.get(id));
    }

    @Override
    public List<ItemListing> findAvailableByCategory(String category) {
        return database.values().stream()
                .filter(ItemListing::isAvailable)
                .filter(listing -> category == null || listing.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    @Override
    public ItemListing save(ItemListing listing) {
        if (listing.getId() == null) {
            listing.setId(idCounter.getAndIncrement());
        }
        database.put(listing.getId(), listing);
        return listing;
    }
}
