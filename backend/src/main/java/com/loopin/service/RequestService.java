package com.loopin.service;

import com.loopin.model.ItemListing;
import com.loopin.model.ItemRequest;

import java.util.List;

/**
 * Business Layer Interface defining operations and business logic
 * for item requests in LoopIn.
 */
public interface RequestService {

    List<ItemRequest> getAllRequests();

    ItemRequest getRequestById(Long id);

    ItemRequest createRequest(ItemRequest request);

    ItemRequest updateRequestStatus(Long id, String newStatus);

    List<ItemListing> findMatchingListings(Long requestId);
}
