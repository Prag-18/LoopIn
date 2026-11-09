package com.loopin.service;

import com.loopin.model.ItemListing;
import com.loopin.model.ItemRequest;
import com.loopin.model.User;

import java.util.List;
import java.util.Map;

/**
 * Business Layer Interface defining operations and business logic
 * for item requests and user transactions in LoopIn.
 */
public interface RequestService {

    List<ItemRequest> getAllRequests();

    ItemRequest getRequestById(Long id);

    ItemRequest createRequest(ItemRequest request);

    ItemRequest updateRequestStatus(Long id, String newStatus);

    List<ItemListing> findMatchingListings(Long requestId);

    List<ItemRequest> getRequestsByUser(Long userId);

    List<ItemRequest> getRequestsByUserEmail(String email);

    Map<String, Object> getBorrowingStatistics();

    List<User> getAllUsers();

    User getUserById(Long userId);
}
