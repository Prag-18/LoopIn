package com.loopin.repository;

import com.loopin.model.ItemRequest;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Layer Interface for ItemRequest operations.
 */
public interface RequestRepository {

    ItemRequest save(ItemRequest request);

    Optional<ItemRequest> findById(Long id);

    List<ItemRequest> findAll();

    List<ItemRequest> findByStatus(String status);

    boolean deleteById(Long id);
}
