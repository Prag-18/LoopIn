import React from 'react';
import { NavLink } from 'react-router-dom';

export default function Hero({ eyebrow, title, subtitle, activeMode }) {
  return (
    <section className="hero-section">
      {eyebrow && <p className="eyebrow">{eyebrow}</p>}
      {title && <h1>{title}</h1>}
      {subtitle && <p className="subtitle">{subtitle}</p>}
      
      {activeMode !== 'none' && (
        <nav className="mode-switcher" aria-label="Gear options">
          <NavLink
            to="/listings"
            className={({ isActive }) => `mode-btn ${isActive ? 'active' : ''}`}
          >
            🔍 Explore Gear
          </NavLink>
          <NavLink
            to="/hosted"
            className={({ isActive }) => `mode-btn ${isActive ? 'active' : ''}`}
          >
            📦 My Hosted Items
          </NavLink>
          <NavLink
            to="/request"
            className={({ isActive }) => `mode-btn ${isActive ? 'active' : ''}`}
          >
            + Post Request
          </NavLink>
        </nav>
      )}
    </section>
  );
}
