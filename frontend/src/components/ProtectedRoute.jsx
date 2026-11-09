import React from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { useApp } from '../context/AppContext';

/**
 * Wraps any route that requires the user to be signed in.
 * If not authenticated, redirects to /signin and saves the
 * intended path in location.state so the user lands there after login.
 */
export default function ProtectedRoute({ children }) {
  const { profile } = useApp();
  const location = useLocation();

  if (!profile) {
    return <Navigate to="/signin" state={{ from: location }} replace />;
  }

  return children;
}

