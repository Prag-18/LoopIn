package com.loopin.repository;

import com.loopin.model.ItemListing;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Layer Interface for ItemListing operations.
 */
public interface ListingRepository {

    List<ItemListing> findAll();

    Optional<ItemListing> findById(Long id);

    List<ItemListing> findAvailableByCategory(String category);

    ItemListing save(ItemListing listing);
}
