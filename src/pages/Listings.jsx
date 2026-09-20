import React from 'react';
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
  const { requests, defaultListings, withdrawRequest, interactiveStates, setActionCompleted } = useApp();

  const totalCount = requests.length + defaultListings.length;

  const getConnectionConfig = (degree) => {
    switch (degree) {
      case 1:
        return { label: '1st degree', badgeClass: 'badge-1st', proof: 'Your friends and classmates' };
      case 2:
        return { label: '2nd degree', badgeClass: 'badge-2nd', proof: 'Friends of friends' };
      case 3:
      default:
        return { label: 'Campus-wide', badgeClass: 'badge-3rd', proof: 'Visible across campus' };
    }
  };

  return (
    <>
      <Hero
        eyebrow="Borrow nearby"
        title="Useful stuff, close to you."
        subtitle="Gear from people in your campus community — shown closest connection first."
        activeMode="listings"
      />
      <main className="main-container single-column">
        <section className="panel-card matches-panel">
          <div className="panel-header">
            <div>
              <p className="section-kicker">Around you</p>
              <h2>Available now</h2>
            </div>
            <span className="badge-count">{totalCount} nearby</span>
          </div>

          <div className="match-list">
            {/* User Created Requests */}
            {requests.map((req) => {
              const config = getConnectionConfig(req.degree);
              const isActioned = interactiveStates[req.id];

              return (
                <article key={req.id} className={`match-card degree-${req.degree}`}>
                  <div className={`trust-badge ${config.badgeClass}`}>{config.label}</div>
                  <div className="card-content">
                    <div className="item-info">
                      <h3>{req.item}</h3>
                      <p className="owner-meta">{req.owner}</p>
                      <div className="trust-proof">
                        <span aria-hidden="true">◌</span> {config.proof} · {formatDate(req.start)}–{formatDate(req.end)}
                      </div>
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
                        <button
                          className="withdraw-btn"
                          type="button"
                          onClick={() => withdrawRequest(req.id)}
                        >
                          Withdraw request
                        </button>
                      </div>
                    </div>
                  </div>
                </article>
              );
            })}

            {/* Default Available Listings */}
            {defaultListings.map((item) => {
              const isActioned = interactiveStates[item.id];
              const badgeClass =
                item.degree === 1 ? 'badge-1st' : item.degree === 2 ? 'badge-2nd' : 'badge-3rd';
              const badgeLabel =
                item.degree === 1 ? '1st degree' : item.degree === 2 ? '2nd degree' : 'Same building';

              return (
                <article key={item.id} className={`match-card degree-${item.degree}`}>
                  <div className={`trust-badge ${badgeClass}`}>{badgeLabel}</div>
                  <div className="card-content">
                    <div className="item-info">
                      <h3>{item.item}</h3>
                      <p className="owner-meta">{item.owner}</p>
                      <div className="trust-proof">
                        <span className="icon" aria-hidden="true">
                          {item.trustType}
                        </span>{' '}
                        <span dangerouslySetInnerHTML={{ __html: item.trustProof }} />
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
