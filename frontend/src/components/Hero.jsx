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
            to="/request"
            className={({ isActive }) => `mode-btn ${isActive ? 'active' : ''}`}
          >
            I need something
          </NavLink>
          <NavLink
            to="/listings"
            className={({ isActive }) => `mode-btn ${isActive ? 'active' : ''}`}
          >
            Browse gear
          </NavLink>
        </nav>
      )}
    </section>
  );
}
