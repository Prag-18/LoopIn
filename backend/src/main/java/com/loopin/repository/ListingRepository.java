package com.loopin.repository;

import com.loopin.model.ItemListing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA Repository for ItemListing entity.
 */
@Repository
public interface ListingRepository extends JpaRepository<ItemListing, Long> {

    List<ItemListing> findByAvailableTrue();

    List<ItemListing> findByAvailableTrueAndCategoryIgnoreCase(String category);
}
