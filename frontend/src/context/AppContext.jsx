import React, { createContext, useContext, useState, useEffect } from 'react';

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

  const [interactiveStates, setInteractiveStates] = useState({});

  useEffect(() => {
    try {
      localStorage.setItem('loopin-requests', JSON.stringify(requests));
    } catch (e) {
      console.error('Failed to save requests:', e);
    }
  }, [requests]);

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

  const addRequest = (newReq) => {
    const requestItem = {
      id: Date.now(),
      item: newReq.item,
      start: newReq.start,
      end: newReq.end,
      degree: Number(newReq.degree),
      owner: `${profile?.name || 'Pragati'} is looking to borrow this`,
      createdAt: new Date().toISOString(),
    };
    setRequests((prev) => [requestItem, ...prev]);
    return requestItem.id;
  };

  const withdrawRequest = (id) => {
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
        defaultListings: INITIAL_GEAR_LISTINGS,
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
