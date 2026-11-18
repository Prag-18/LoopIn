import React, { useState } from 'react';
import { Link, NavLink } from 'react-router-dom';
import { useApp } from '../context/AppContext';
import CircleModal from './CircleModal';

export default function Navbar() {
  const { profile, signOut, friends, requests, defaultListings } = useApp();
  const [isCircleOpen, setIsCircleOpen] = useState(false);

  const userEmail = (profile?.email || '').toLowerCase();
  const myHostedCount = userEmail
    ? requests.filter((r) => (r.requesterEmail || '').toLowerCase() === userEmail).length +
      defaultListings.filter((l) => (l.ownerEmail || '').toLowerCase() === userEmail).length
    : 0;

  return (
    <>
      <header className="navbar">
        <Link to="/" className="logo-container" aria-label="LoopIn home">
          <span className="logo-icon" aria-hidden="true">↔</span>
          <span className="logo-text">LoopIn</span>
        </Link>

        <nav className="nav-links" aria-label="Main navigation">
          <NavLink to="/listings">Explore Gear</NavLink>
          <NavLink to="/hosted" className="hosted-nav-link">
            My Hosted Items
            {myHostedCount > 0 && <span className="nav-pill-count">{myHostedCount}</span>}
          </NavLink>
          <NavLink to="/request">Post a Request</NavLink>
        </nav>

        <nav className="auth-links" aria-label="Account">
          {profile ? (
            <div style={{ display: 'flex', gap: '8px', alignItems: 'center' }}>
              <button
                type="button"
                className="circle-nav-btn"
                onClick={() => setIsCircleOpen(true)}
                title="View your 1st-degree campus circle"
              >
                👥 Circle ({friends.length})
              </button>

              <div className="user-profile-badge">
                <div className="verified-dot" title="Verified Student"></div>
                <span className="user-avatar">{profile.name.charAt(0).toUpperCase()}</span>
                <span>{profile.name}</span>
                <button className="signout-btn" onClick={signOut} type="button">
                  Sign out
                </button>
              </div>
            </div>
          ) : (
            <>
              <Link to="/signin">Sign in</Link>
              <Link className="auth-signup" to="/signup">
                Create account
              </Link>
            </>
          )}
        </nav>
      </header>

      <CircleModal isOpen={isCircleOpen} onClose={() => setIsCircleOpen(false)} />
    </>
  );
}
