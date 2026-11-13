import React, { createContext, useContext, useState, useEffect } from 'react';
import * as api from '../api/api';

const AppContext = createContext();

const INITIAL_GEAR_LISTINGS = [
  {
    id: 'default-1',
    item: 'Canon EOS Rebel T7 DSLR',
    owner: 'Aarav Mehta · CSE ’26',
    trustProof: 'You share Data Structures',
    trustType: '◉',
    degree: 1,
    price: '₹800',
    unit: 'a day',
    isDefault: true,
  },
  {
    id: 'default-2',
    item: 'Casio fx-991ES Plus',
    owner: 'Kavya Iyer · ECE ’25',
    trustProof: 'Mutual: Rohan Sharma · 2 past borrows',
    trustType: '◌',
    degree: 2,
    price: '₹100',
    unit: 'a day',
    isDefault: true,
  },
  {
    id: 'default-3',
    item: 'Heavy-duty extension cord · 15 m',
    owner: 'Ishita Verma · Mechanical ’27',
    trustProof: 'Also in Tagore Hostel',
    trustType: '⌂',
    degree: 3,
    price: '₹50',
    unit: 'a day',
    isDefault: true,
  },
];

export function AppProvider({ children }) {
  const [profile, setProfile] = useState(() => {
    try {
      const saved = localStorage.getItem('loopin-profile');
      return saved ? JSON.parse(saved) : null;
    } catch {
      return null;
    }
  });

  const [requests, setRequests] = useState(() => {
    try {
      const saved = JSON.parse(localStorage.getItem('loopin-requests') || '[]');
      return Array.isArray(saved) ? saved : [];
    } catch {
      return [];
    }
  });

  const [listings, setListings] = useState(INITIAL_GEAR_LISTINGS);
  const [backendConnected, setBackendConnected] = useState(false);

  const [interactiveStates, setInteractiveStates] = useState(() => {
    try {
      const saved = JSON.parse(localStorage.getItem('loopin-interactions') || '{}');
      return typeof saved === 'object' && saved !== null ? saved : {};
    } catch {
      return {};
    }
  });

  // Sync requests and listings from Spring Boot backend on mount
  useEffect(() => {
    let isMounted = true;

    async function loadBackendData() {
      // 1. Fetch Item Requests from /api/requests
      try {
        const liveRequests = await api.fetchRequests();
        if (isMounted && Array.isArray(liveRequests)) {
          const formatted = liveRequests
            .filter((r) => r.status !== 'CANCELLED')
            .map((r) => ({
              id: r.id,
              item: r.itemName,
              category: r.category,
              start: r.startDate,
              end: r.endDate,
              degree: r.trustRadius,
              owner: `${r.requesterName || 'Campus Student'} is looking to borrow this`,
              requesterEmail: r.requesterEmail,
              status: r.status,
              trustVerificationStatus: r.trustVerificationStatus,
              note: r.note,
              createdAt: r.createdAt,
            }));
          setRequests(formatted);
          setBackendConnected(true);
        }
      } catch (err) {
        console.warn('[LoopIn] Backend requests API unreachable, using local storage cache:', err.message);
        setBackendConnected(false);
      }

      // 2. Fetch Listings from /api/listings
      try {
        const liveListings = await api.fetchListings();
        if (isMounted && Array.isArray(liveListings) && liveListings.length > 0) {
          const formattedListings = liveListings.map((l) => ({
            id: `listing-${l.id}`,
            rawId: l.id,
            item: l.title,
            category: l.category,
            owner: `${l.ownerName} · ${l.category || 'Campus'}`,
            ownerEmail: l.ownerEmail,
            trustProof: `Verified Campus Owner · ${l.condition || 'GOOD'} condition`,
            trustType: l.minTrustDegree === 1 ? '◉' : l.minTrustDegree === 2 ? '◌' : '⌂',
            degree: l.minTrustDegree,
            price: 'Free',
            unit: 'to borrow',
            available: l.available,
          }));
          setListings(formattedListings);
        }
      } catch (err) {
        console.warn('[LoopIn] Backend listings API unreachable, using default items:', err.message);
      }
    }

    loadBackendData();

    return () => {
      isMounted = false;
    };
  }, []);

  // Save requests to localStorage as an offline cache
  useEffect(() => {
    try {
      localStorage.setItem('loopin-requests', JSON.stringify(requests));
    } catch (e) {
      console.error('Failed to save requests:', e);
    }
  }, [requests]);

  // Save interactive button states
  useEffect(() => {
    try {
      localStorage.setItem('loopin-interactions', JSON.stringify(interactiveStates));
    } catch (e) {
      console.error('Failed to save interactions:', e);
    }
  }, [interactiveStates]);

  // Save authenticated user profile
  useEffect(() => {
    try {
      if (profile) {
        localStorage.setItem('loopin-profile', JSON.stringify(profile));
      } else {
        localStorage.removeItem('loopin-profile');
      }
    } catch (e) {
      console.error('Failed to save profile:', e);
    }
  }, [profile]);

  /**
   * Add a new borrow request by calling POST /api/requests on Spring Boot backend
   */
  const addRequest = async (newReq) => {
    const payload = {
      itemName: newReq.item,
      category: newReq.category || 'Academics',
      requesterName: profile?.name || 'Campus Student',
      requesterEmail: profile?.email || 'student@campus.edu',
      startDate: newReq.start,
      endDate: newReq.end,
      trustRadius: Number(newReq.degree) || 2,
      note: newReq.note || '',
      status: 'OPEN',
    };

    try {
      const created = await api.createRequest(payload);
      const requestItem = {
        id: created.id,
        item: created.itemName,
        category: created.category,
        start: created.startDate,
        end: created.endDate,
        degree: created.trustRadius,
        owner: `${created.requesterName || profile?.name || 'You'} is looking to borrow this`,
        requesterEmail: created.requesterEmail,
        status: created.status,
        trustVerificationStatus: created.trustVerificationStatus,
        createdAt: created.createdAt || new Date().toISOString(),
      };

      setRequests((prev) => [requestItem, ...prev]);
      setBackendConnected(true);
      return created.id;
    } catch (err) {
      // If server returned a business rule validation error (e.g., 30-day limit, academic domain), throw it
      if (err.message && !err.message.includes('Failed to fetch')) {
        throw err;
      }

      // Offline fallback
      console.warn('[LoopIn] Backend offline, saving request locally:', err.message);
      const fallbackItem = {
        id: Date.now(),
        item: newReq.item,
        category: newReq.category || 'General',
        start: newReq.start,
        end: newReq.end,
        degree: Number(newReq.degree),
        owner: `${profile?.name || 'You'} is looking to borrow this`,
        requesterEmail: profile?.email,
        status: 'OPEN',
        trustVerificationStatus: 'OFFLINE_MODE',
        createdAt: new Date().toISOString(),
      };
      setRequests((prev) => [fallbackItem, ...prev]);
      return fallbackItem.id;
    }
  };

  /**
   * Withdraw / Cancel request by calling PUT /api/requests/{id}/status?status=CANCELLED
   */
  const withdrawRequest = async (id) => {
    try {
      if (typeof id === 'number') {
        await api.updateRequestStatus(id, 'CANCELLED');
      }
    } catch (err) {
      console.warn('[LoopIn] Failed to cancel request on backend:', err.message);
    }
    setRequests((prev) => prev.filter((r) => r.id !== id));
  };

  const signUp = (name, email) => {
    const user = { name, email };
    setProfile(user);
  };

  const signIn = (email) => {
    const name = email.split('@')[0];
    const capitalizedName = name.charAt(0).toUpperCase() + name.slice(1);
    const user = { name: capitalizedName, email };
    setProfile(user);
  };

  const signOut = () => {
    setProfile(null);
  };

  const setActionCompleted = (id) => {
    setInteractiveStates((prev) => ({ ...prev, [id]: true }));
  };

  return (
    <AppContext.Provider
      value={{
        profile,
        requests,
        defaultListings: listings,
        backendConnected,
        interactiveStates,
        addRequest,
        withdrawRequest,
        signUp,
        signIn,
        signOut,
        setActionCompleted,
      }}
    >
      {children}
    </AppContext.Provider>
  );
}

export function useApp() {
  const context = useContext(AppContext);
  if (!context) {
    throw new Error('useApp must be used within an AppProvider');
  }
  return context;
}
