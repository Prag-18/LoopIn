import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import Hero from '../components/Hero';
import HostItemModal from '../components/HostItemModal';
import { useApp } from '../context/AppContext';

function formatDate(value) {
  if (!value) return '';
  try {
    return new Intl.DateTimeFormat('en', { month: 'short', day: 'numeric' }).format(
      new Date(`${value}T00:00:00`)
    );
  } catch {
    return value;
  }
}

export default function HostedItems() {
  const {
    profile,
    requests,
    defaultListings,
    withdrawRequest,
    withdrawListing,
    backendConnected,
    friends,
  } = useApp();

  const [isHostModalOpen, setIsHostModalOpen] = useState(false);
  const [activeFilter, setActiveFilter] = useState('ALL'); // 'ALL' | 'LISTINGS' | 'REQUESTS'
  const [toastMessage, setToastMessage] = useState('');

  const userEmail = (profile?.email || '').toLowerCase();

  // 1. Hosted gear (items where owner is the current user)
  const myListings = defaultListings.filter((l) => {
    const ownerEmail = (l.ownerEmail || '').toLowerCase();
    return userEmail && ownerEmail === userEmail;
  });

  // 2. Active requests posted by the current user
  const myRequests = requests.filter((r) => {
    const requesterEmail = (r.requesterEmail || '').toLowerCase();
    return userEmail && requesterEmail === userEmail;
  });

  const totalHostedCount = myListings.length + myRequests.length;

  const showToast = (msg) => {
    setToastMessage(msg);
    setTimeout(() => setToastMessage(''), 3500);
  };

  const handleWithdrawListing = (id, rawId, title) => {
    withdrawListing(id, rawId);
    showToast(`Removed "${title}" from campus listings.`);
  };

  const handleWithdrawRequest = (id, title) => {
    withdrawRequest(id);
    showToast(`Withdrew request for "${title}".`);
  };

  const getRadiusBadge = (degree) => {
    if (degree === 1) return { label: '1st Degree (Friends Only)', badgeClass: 'badge-1st' };
    if (degree === 2) return { label: '2nd Degree (Mutuals)', badgeClass: 'badge-2nd' };
    return { label: 'Campus-wide (All Students)', badgeClass: 'badge-3rd' };
  };

  return (
    <>
      <Hero
        eyebrow="My Campus Space"
        title="My Hosted Items"
        subtitle="Manage the gear you're lending out and active borrow requests you've posted on LoopIn."
        activeMode="hosted"
      />

      <main className="main-container single-column">
        {toastMessage && (
          <div className="friend-toast-notification">
            <span>{toastMessage}</span>
          </div>
        )}

        {/* Top Control Bar & Segmented Navigation */}
        <div className="hosted-header-bar">
          <div className="view-mode-tabs">
            <Link to="/listings" className="view-tab">
              🔍 Explore Campus Gear
            </Link>
            <span className="view-tab active">
              📦 My Hosted Items ({totalHostedCount})
            </span>
          </div>

          <div style={{ display: 'flex', gap: '8px' }}>
            <button
              type="button"
              className="primary-btn"
              onClick={() => setIsHostModalOpen(true)}
              style={{ width: 'auto', padding: '8px 16px', fontSize: '13px' }}
            >
              + Host New Gear
            </button>
            <Link
              to="/request"
              className="secondary-btn button-link"
              style={{ width: 'auto', padding: '8px 16px', fontSize: '13px' }}
            >
              + Post Request
            </Link>
          </div>
        </div>

        {/* Quick Stats Overview */}
        <section className="panel-card hosted-stats-panel" style={{ padding: '20px 24px', marginBottom: '24px' }}>
          <div className="hosted-stats-grid">
            <div className="hosted-stat-card">
              <span className="stat-label">Total Hosted Items</span>
              <span className="stat-value">{totalHostedCount}</span>
              <small className="stat-sub">Active on LoopIn</small>
            </div>
            <div className="hosted-stat-card">
              <span className="stat-label">Gear Offered</span>
              <span className="stat-value">{myListings.length}</span>
              <small className="stat-sub">Available to classmates</small>
            </div>
            <div className="hosted-stat-card">
              <span className="stat-label">Active Requests</span>
              <span className="stat-value">{myRequests.length}</span>
              <small className="stat-sub">Waiting for offers</small>
            </div>
            <div className="hosted-stat-card">
              <span className="stat-label">Trust Circle</span>
              <span className="stat-value">{friends.length}</span>
              <small className="stat-sub">Direct 1st-degree peers</small>
            </div>
          </div>
        </section>

        {/* Hosted Items Content */}
        <section className="panel-card">
          <div className="panel-header">
            <div>
              <p className="section-kicker">Manage Your Network Items</p>
              <h2>Items you are hosting</h2>
            </div>

            <div className="filter-pill-group">
              <button
                type="button"
                className={`filter-pill ${activeFilter === 'ALL' ? 'active' : ''}`}
                onClick={() => setActiveFilter('ALL')}
              >
                All ({totalHostedCount})
              </button>
              <button
                type="button"
                className={`filter-pill ${activeFilter === 'LISTINGS' ? 'active' : ''}`}
                onClick={() => setActiveFilter('LISTINGS')}
              >
                Offered Gear ({myListings.length})
              </button>
              <button
                type="button"
                className={`filter-pill ${activeFilter === 'REQUESTS' ? 'active' : ''}`}
                onClick={() => setActiveFilter('REQUESTS')}
              >
                Borrow Requests ({myRequests.length})
              </button>
            </div>
          </div>

          {totalHostedCount === 0 ? (
            <div className="empty-state" style={{ padding: '48px 24px' }}>
              <div style={{ fontSize: '42px', marginBottom: '12px' }}>📦</div>
              <h3 style={{ fontSize: '18px', fontWeight: '700', marginBottom: '8px' }}>
                You have no items hosted yet
              </h3>
              <p style={{ maxWidth: '420px', margin: '0 auto 20px', color: 'var(--muted)', fontSize: '14px' }}>
                Keep your campus sharing circle thriving! Share a calculator, camera, charger, or textbooks with peers.
              </p>
              <div style={{ display: 'flex', gap: '12px', justifyContent: 'center', flexWrap: 'wrap' }}>
                <button
                  type="button"
                  className="primary-btn"
                  onClick={() => setIsHostModalOpen(true)}
                  style={{ width: 'auto' }}
                >
                  + Host Gear for Campus
                </button>
                <Link to="/request" className="secondary-btn button-link" style={{ width: 'auto' }}>
                  + Post a Borrow Request
                </Link>
                <Link to="/listings" className="secondary-btn button-link" style={{ width: 'auto' }}>
                  🔍 Explore Community Gear
                </Link>
              </div>
            </div>
          ) : (
            <div className="match-list">
              {/* 1. User's Gear Listings */}
              {(activeFilter === 'ALL' || activeFilter === 'LISTINGS') &&
                myListings.map((item) => {
                  const badge = getRadiusBadge(item.degree);
                  return (
                    <article key={item.id} className="match-card degree-1 hosted-item-card">
                      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '8px' }}>
                        <div style={{ display: 'flex', gap: '8px', alignItems: 'center' }}>
                          <span className="hosted-type-pill pill-offered">✦ Hosted by You</span>
                          <div className={`trust-badge ${badge.badgeClass}`}>{badge.label}</div>
                        </div>
                        {item.category && (
                          <span style={{ fontSize: '11px', color: 'var(--muted)', fontWeight: '600' }}>
                            {item.category}
                          </span>
                        )}
                      </div>

                      <div className="card-content">
                        <div className="item-info">
                          <h3>{item.item}</h3>
                          <div className="trust-proof">
                            <span aria-hidden="true">✓</span> Live on Campus Network · {item.trustProof}
                          </div>
                          <div style={{ marginTop: '6px', fontSize: '12px', color: '#16a34a', fontWeight: '600' }}>
                            ● Active & Available for Borrowing
                          </div>
                        </div>

                        <div className="action-block">
                          <button
                            className="withdraw-btn"
                            type="button"
                            onClick={() => handleWithdrawListing(item.id, item.rawId, item.item)}
                            title="Remove this item from community discovery"
                          >
                            Withdraw item
                          </button>
                        </div>
                      </div>
                    </article>
                  );
                })}

              {/* 2. User's Borrow Requests */}
              {(activeFilter === 'ALL' || activeFilter === 'REQUESTS') &&
                myRequests.map((req) => {
                  const badge = getRadiusBadge(req.degree);
                  return (
                    <article key={req.id} className="match-card degree-1 hosted-item-card">
                      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '8px' }}>
                        <div style={{ display: 'flex', gap: '8px', alignItems: 'center' }}>
                          <span className="hosted-type-pill pill-requested">⇄ Your Borrow Request</span>
                          <div className={`trust-badge ${badge.badgeClass}`}>{badge.label}</div>
                        </div>
                        {req.category && (
                          <span style={{ fontSize: '11px', color: 'var(--muted)', fontWeight: '600' }}>
                            {req.category}
                          </span>
                        )}
                      </div>

                      <div className="card-content">
                        <div className="item-info">
                          <h3>{req.item}</h3>
                          <div className="trust-proof">
                            <span aria-hidden="true">◌</span> Looking to borrow · {formatDate(req.start)}–{formatDate(req.end)}
                          </div>
                          {req.note && (
                            <p style={{ margin: '6px 0 0', fontSize: '12px', color: '#475569', fontStyle: 'italic' }}>
                              "{req.note}"
                            </p>
                          )}
                          {req.trustVerificationStatus && (
                            <div style={{ marginTop: '6px', fontSize: '11px', color: '#059669', fontWeight: '600' }}>
                              ✓ Verification: {req.trustVerificationStatus}
                            </div>
                          )}
                        </div>

                        <div className="action-block">
                          <button
                            className="withdraw-btn"
                            type="button"
                            onClick={() => handleWithdrawRequest(req.id, req.item)}
                            title="Withdraw this borrow request"
                          >
                            Withdraw request
                          </button>
                        </div>
                      </div>
                    </article>
                  );
                })}
            </div>
          )}
        </section>
      </main>

      <HostItemModal
        isOpen={isHostModalOpen}
        onClose={() => setIsHostModalOpen(false)}
      />
    </>
  );
}
