import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Hero from '../components/Hero';
import { useApp } from '../context/AppContext';

export default function Request() {
  const navigate = useNavigate();
  const { addRequest } = useApp();

  const today = new Date().toISOString().slice(0, 10);
  const [itemName, setItemName] = useState('');
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');
  const [trustRadius, setTrustRadius] = useState('2');
  const [errorMsg, setErrorMsg] = useState('');

  const handleStartDateChange = (e) => {
    const val = e.target.value;
    setStartDate(val);
    if (endDate && endDate < val) {
      setEndDate(val);
    }
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!itemName.trim()) {
      setErrorMsg('Please enter what you are looking for.');
      return;
    }
    if (endDate < startDate) {
      setErrorMsg('End date must be on or after the start date.');
      return;
    }

    addRequest({
      item: itemName.trim(),
      start: startDate,
      end: endDate,
      degree: trustRadius,
    });

    navigate('/listings');
  };

  return (
    <>
      <Hero
        eyebrow="Need to borrow?"
        title="Ask your campus circle."
        subtitle="We’ll start close to home and only widen the search if you choose."
        activeMode="request"
      />
      <main className="main-container single-column">
        <section className="panel-card request-panel">
          <p className="section-kicker">New request</p>
          <h2>A few details and you’re set.</h2>
          {errorMsg && (
            <div style={{ color: '#b03a3a', fontSize: '14px', marginBottom: '12px' }}>
              {errorMsg}
            </div>
          )}
          <form className="request-form" onSubmit={handleSubmit}>
            <div className="form-group">
              <label htmlFor="item-name">What are you looking for?</label>
              <input
                type="text"
                id="item-name"
                placeholder="Camera, calculator, tripod…"
                autoComplete="off"
                required
                value={itemName}
                onChange={(e) => {
                  setItemName(e.target.value);
                  setErrorMsg('');
                }}
              />
            </div>
            <div className="form-row">
              <div className="form-group">
                <label htmlFor="date-start">Start date</label>
                <input
                  type="date"
                  id="date-start"
                  min={today}
                  required
                  value={startDate}
                  onChange={handleStartDateChange}
                />
              </div>
              <div className="form-group">
                <label htmlFor="date-end">End date</label>
                <input
                  type="date"
                  id="date-end"
                  min={startDate || today}
                  required
                  value={endDate}
                  onChange={(e) => {
                    setEndDate(e.target.value);
                    setErrorMsg('');
                  }}
                />
              </div>
            </div>
            <div className="form-group">
              <label htmlFor="trust-radius">Who should see this?</label>
              <select
                id="trust-radius"
                value={trustRadius}
                onChange={(e) => setTrustRadius(e.target.value)}
              >
                <option value="1">My friends and classmates</option>
                <option value="2">Friends of friends</option>
                <option value="3">Anyone on campus</option>
              </select>
            </div>
            <button type="submit" className="primary-btn">
              Share request <span aria-hidden="true">→</span>
            </button>
            <p className="form-note">
              You can always take it down later. We’ll never share your email address.
            </p>
          </form>
        </section>
      </main>
    </>
  );
}
