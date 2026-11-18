import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import Hero from '../components/Hero';
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

export default function Listings() {
  const {
    requests,
    defaultListings,
    interactiveStates,
    setActionCompleted,
    profile,
    backendConnected,
    addFriend,
    removeFriend,
    isFriend,
  } = useApp();

  const [feedbackToast, setFeedbackToast] = useState('');

  const userEmail = (profile?.email || '').toLowerCase();
  const isOwnItem = (email) => Boolean(userEmail && (email || '').toLowerCase() === userEmail);

  // Separate explore items (from other campus members) from user's own hosted items
  const communityRequests = requests.filter((r) => !isOwnItem(r.requesterEmail));
  const communityListings = defaultListings.filter((l) => !isOwnItem(l.ownerEmail));

  // Count of user's own hosted items
  const myHostedRequestsCount = requests.filter((r) => isOwnItem(r.requesterEmail)).length;
  const myHostedListingsCount = defaultListings.filter((l) => isOwnItem(l.ownerEmail)).length;
  const myHostedCount = myHostedRequestsCount + myHostedListingsCount;

  const totalExploreCount = communityRequests.length + communityListings.length;

  const getConnectionConfig = (degree, isConnected) => {
    if (isConnected || degree === 1) {
      return {
        label: isConnected ? '1st degree (Friend)' : '1st degree',
        badgeClass: 'badge-1st',
        proof: isConnected ? 'Connected in your 1st-degree friend circle' : 'Your friends and classmates',
      };
    }
    if (degree === 2) {
      return { label: '2nd degree', badgeClass: 'badge-2nd', proof: 'Friends of friends' };
    }
    return { label: 'Campus-wide', badgeClass: 'badge-3rd', proof: 'Visible across campus' };
  };

  const handleFriendToggle = (name, email) => {
    if (isFriend(email)) {
      removeFriend(email);
      setFeedbackToast(`Removed ${name} from your friend circle`);
    } else {
      addFriend({ name, email });
      setFeedbackToast(`🎉 Connected with ${name}! Their gear is now 1st degree.`);
    }
    setTimeout(() => setFeedbackToast(''), 3500);
  };

  return (
    <>
      <Hero
        eyebrow="Borrow nearby"
        title="Explore Campus Gear"
        subtitle="Gear available from students in your campus community — shown closest connection first."
        activeMode="listings"
      />
      <main className="main-container single-column">
        {feedbackToast && (
          <div className="friend-toast-notification">
            <span>{feedbackToast}</span>
          </div>
        )}

        {/* Top Control Bar & Segmented Navigation */}
        <div className="hosted-header-bar">
          <div className="view-mode-tabs">
            <span className="view-tab active">
              🔍 Explore Campus Gear ({totalExploreCount})
            </span>
            <Link to="/hosted" className="view-tab">
              📦 My Hosted Items {myHostedCount > 0 ? `(${myHostedCount})` : ''}
            </Link>
          </div>

          <div style={{ display: 'flex', gap: '8px' }}>
            <Link
              to="/hosted"
              className="secondary-btn button-link"
              style={{ width: 'auto', padding: '8px 16px', fontSize: '13px' }}
            >
              Manage My Hosted Gear
            </Link>
            <Link
              to="/request"
              className="primary-btn button-link"
              style={{ width: 'auto', padding: '8px 16px', fontSize: '13px' }}
            >
              + Post Request
            </Link>
          </div>
        </div>

        {/* Informative banner if user has hosted items */}
        {myHostedCount > 0 && (
          <div className="hosted-info-banner">
            <span>
              ℹ️ You have <strong>{myHostedCount} item{myHostedCount > 1 ? 's' : ''}</strong> currently hosted on the campus network.
            </span>
            <Link to="/hosted" className="banner-link">
              View your hosted items →
            </Link>
          </div>
        )}

        <section className="panel-card matches-panel">
          <div className="panel-header">
            <div>
              <p className="section-kicker">Around you on campus</p>
              <h2>Available to borrow</h2>
            </div>
            <div style={{ display: 'flex', gap: '10px', alignItems: 'center' }}>
              <span
                style={{
                  fontSize: '11px',
                  fontWeight: '600',
                  padding: '3px 8px',
                  borderRadius: '12px',
                  backgroundColor: backendConnected ? '#dcfce7' : '#fef3c7',
                  color: backendConnected ? '#15803d' : '#b45309',
                }}
                title={backendConnected ? 'Connected to Spring Boot REST backend on port 8080' : 'Backend offline - using local cache'}
              >
                {backendConnected ? '● Live Backend' : '○ Offline Cache'}
              </span>
              <span className="badge-count">{totalExploreCount} available</span>
            </div>
          </div>

          {totalExploreCount === 0 && (
            <div className="empty-state">
              <p>No community items found nearby right now.</p>
              <div style={{ display: 'flex', gap: '10px', justifyContent: 'center', marginTop: '12px' }}>
                <Link to="/hosted" className="primary-btn button-link" style={{ display: 'inline-flex', width: 'auto' }}>
                  + Host your gear for campus
                </Link>
                <Link to="/request" className="secondary-btn button-link" style={{ display: 'inline-flex', width: 'auto' }}>
                  Post a request →
                </Link>
              </div>
            </div>
          )}

          <div className="match-list">
            {/* 1. Community Borrow Requests (synced from /api/requests, excluding user's own) */}
            {communityRequests.map((req) => {
              const hostEmail = req.requesterEmail || 'student@campus.edu';
              const hostName =
                req.requesterName ||
                (req.owner ? req.owner.replace(' is looking to borrow this', '').split(' · ')[0] : 'Campus Student');
              const isConnected = isFriend(hostEmail);
              const config = getConnectionConfig(req.degree, isConnected);
              const isActioned = interactiveStates[req.id];

              return (
                <article key={req.id} className={`match-card degree-${isConnected ? 1 : req.degree}`}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '8px' }}>
                    <div className={`trust-badge ${config.badgeClass}`}>{config.label}</div>
                    {req.category && (
                      <span style={{ fontSize: '11px', color: 'var(--muted)', fontWeight: '600' }}>
                        {req.category}
                      </span>
                    )}
                  </div>
                  <div className="card-content">
                    <div className="item-info">
                      <h3>{req.item}</h3>

                      <div className="owner-row">
                        <span className="owner-meta">{req.owner}</span>
                        {profile && (
                          <button
                            type="button"
                            className={`friend-btn ${isConnected ? 'friend-active' : 'friend-add'}`}
                            onClick={() => handleFriendToggle(hostName, hostEmail)}
                            title={isConnected ? 'Direct friend (click to remove)' : `Add ${hostName} to your 1st-degree circle`}
                          >
                            {isConnected ? '✓ Friend' : '+ Add Friend'}
                          </button>
                        )}
                      </div>

                      <div className="trust-proof">
                        <span aria-hidden="true">◌</span> {config.proof} · {formatDate(req.start)}–{formatDate(req.end)}
                      </div>

                      {req.trustVerificationStatus && (
                        <div style={{ marginTop: '6px', fontSize: '11px', color: '#059669', fontWeight: '600' }}>
                          ✓ Trust Status: {req.trustVerificationStatus}
                        </div>
                      )}

                      {req.note && (
                        <p style={{ margin: '6px 0 0', fontSize: '12px', color: '#475569', fontStyle: 'italic' }}>
                          "{req.note}"
                        </p>
                      )}
                    </div>

                    <div className="action-block">
                      <span className="price">Have one?</span>
                      <div className="request-actions">
                        <button
                          className="secondary-btn"
                          type="button"
                          disabled={isActioned}
                          onClick={() => setActionCompleted(req.id)}
                        >
                          {isActioned ? 'Offer sent' : 'Offer yours'}
                        </button>
                      </div>
                    </div>
                  </div>
                </article>
              );
            })}

            {/* 2. Community Available Listings (synced from /api/listings, excluding user's own) */}
            {communityListings.map((item) => {
              const isActioned = interactiveStates[item.id];
              const hostName = item.ownerName || (item.owner ? item.owner.split(' · ')[0] : 'Campus Lender');
              const hostEmail =
                item.ownerEmail ||
                (item.ownerName
                  ? item.ownerName.toLowerCase().replace(/\s+/g, '.') + '@campus.edu'
                  : 'lender@campus.edu');
              const isConnected = isFriend(hostEmail);

              const badgeClass = isConnected || item.degree === 1 ? 'badge-1st' : item.degree === 2 ? 'badge-2nd' : 'badge-3rd';
              const badgeLabel = isConnected
                ? '1st degree (Friend)'
                : item.degree === 1
                ? '1st degree'
                : item.degree === 2
                ? '2nd degree'
                : 'Same building';

              return (
                <article key={item.id} className={`match-card degree-${isConnected ? 1 : item.degree}`}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '8px' }}>
                    <div className={`trust-badge ${badgeClass}`}>{badgeLabel}</div>
                    {item.category && (
                      <span style={{ fontSize: '11px', color: 'var(--muted)', fontWeight: '600' }}>
                        {item.category}
                      </span>
                    )}
                  </div>

                  <div className="card-content">
                    <div className="item-info">
                      <h3>{item.item}</h3>

                      <div className="owner-row">
                        <span className="owner-meta">{item.owner}</span>
                        {profile && (
                          <button
                            type="button"
                            className={`friend-btn ${isConnected ? 'friend-active' : 'friend-add'}`}
                            onClick={() => handleFriendToggle(hostName, hostEmail)}
                            title={isConnected ? 'Direct friend (click to remove)' : `Add ${hostName} to your 1st-degree circle`}
                          >
                            {isConnected ? '✓ Friend' : '+ Add Friend'}
                          </button>
                        )}
                      </div>

                      <div className="trust-proof">
                        <span className="icon" aria-hidden="true">
                          {isConnected ? '◉' : item.trustType}
                        </span>{' '}
                        {isConnected ? (
                          <span>Connected in your 1st-degree circle</span>
                        ) : (
                          <span dangerouslySetInnerHTML={{ __html: item.trustProof }} />
                        )}
                      </div>
                    </div>

                    <div className="action-block">
                      <span className="price">
                        {item.price} <small>{item.unit}</small>
                      </span>
                      <button
                        className="secondary-btn"
                        type="button"
                        disabled={isActioned}
                        onClick={() => setActionCompleted(item.id)}
                      >
                        {isActioned ? 'Request sent' : 'Ask to borrow'}
                      </button>
                    </div>
                  </div>
                </article>
              );
            })}
          </div>
        </section>
      </main>
    </>
  );
}
