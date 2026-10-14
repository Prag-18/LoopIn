import React from 'react';
import { Link } from 'react-router-dom';
import { useApp } from '../context/AppContext';

export default function Navbar() {
  const { profile, signOut } = useApp();

  return (
    <header className="navbar">
      <Link to="/" className="logo-container" aria-label="LoopIn home">
        <span className="logo-icon" aria-hidden="true">↔</span>
        <span className="logo-text">LoopIn</span>
      </Link>

      <nav className="auth-links" aria-label="Account">
        {profile ? (
          <div className="user-profile-badge">
            <div className="verified-dot" title="Verified Student"></div>
            <span className="user-avatar">{profile.name.charAt(0).toUpperCase()}</span>
            <span>{profile.name}</span>
            <button className="signout-btn" onClick={signOut} type="button">
              Sign out
            </button>
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
  );
}
