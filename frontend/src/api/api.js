// Base API URL: Uses relative path so Vite proxy handles port forwarding in dev,
// and Tomcat / production handles same-origin requests seamlessly.
const API_BASE = '/api';

/**
 * Fetch all borrow requests from Spring Boot backend (Microservice 1)
 */
export async function fetchRequests() {
  const res = await fetch(`${API_BASE}/requests`);
  if (!res.ok) {
    throw new Error(`Failed to fetch requests: HTTP ${res.status}`);
  }
  return res.json();
}

/**
 * Create a new item request attempt.
 * Evaluated by RequestService (Business Rules 1, 2) and inter-service Trust Verification.
 */
export async function createRequest(payload) {
  const res = await fetch(`${API_BASE}/requests`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(payload),
  });

  const data = await res.json().catch(() => null);

  if (!res.ok) {
    const errorMsg = data?.message || data?.error || `Request failed with HTTP status ${res.status}`;
    throw new Error(errorMsg);
  }

  return data;
}

/**
 * Update request status (e.g. FULFILLED, CANCELLED)
 */
export async function updateRequestStatus(id, status) {
  const res = await fetch(`${API_BASE}/requests/${id}/status?status=${encodeURIComponent(status)}`, {
    method: 'PUT',
  });

  const data = await res.json().catch(() => null);

  if (!res.ok) {
    const errorMsg = data?.message || data?.error || `Status update failed with HTTP ${res.status}`;
    throw new Error(errorMsg);
  }

  return data;
}

/**
 * Fetch community item listings from backend
 */
export async function fetchListings(category) {
  const url = category ? `${API_BASE}/listings?category=${encodeURIComponent(category)}` : `${API_BASE}/listings`;
  const res = await fetch(url);
  if (!res.ok) {
    throw new Error(`Failed to fetch listings: HTTP ${res.status}`);
  }
  return res.json();
}

/**
 * Host a new item listing on campus
 */
export async function createListing(payload) {
  const res = await fetch(`${API_BASE}/listings`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(payload),
  });

  const data = await res.json().catch(() => null);

  if (!res.ok) {
    const errorMsg = data?.message || data?.error || `Failed to create listing: HTTP ${res.status}`;
    throw new Error(errorMsg);
  }

  return data;
}

/**
 * Delete / Withdraw a hosted listing
 */
export async function deleteListing(id) {
  const res = await fetch(`${API_BASE}/listings/${id}`, {
    method: 'DELETE',
  });

  if (!res.ok) {
    throw new Error(`Failed to delete listing: HTTP ${res.status}`);
  }

  return true;
}

/**
 * Fetch aggregate borrowing statistics
 */
export async function fetchStatistics() {
  const res = await fetch(`${API_BASE}/requests/stats`);
  if (!res.ok) {
    throw new Error(`Failed to fetch statistics: HTTP ${res.status}`);
  }
  return res.json();
}

/**
 * Fetch attempt history for a specific student email
 */
export async function fetchUserHistory(email) {
  const res = await fetch(`${API_BASE}/requests/history?email=${encodeURIComponent(email)}`);
  if (!res.ok) {
    throw new Error(`Failed to fetch user history: HTTP ${res.status}`);
  }
  return res.json();
}

