import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Hero from '../components/Hero';
import { useApp } from '../context/AppContext';

export default function Request() {
  const navigate = useNavigate();
  const { addRequest } = useApp();

  const today = new Date().toISOString().slice(0, 10);
  const [itemName, setItemName] = useState('');
  const [category, setCategory] = useState('Academics');
  const [note, setNote] = useState('');
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');
  const [trustRadius, setTrustRadius] = useState('2');
  const [errorMsg, setErrorMsg] = useState('');
  const [submitting, setSubmitting] = useState(false);

  const handleStartDateChange = (e) => {
    const val = e.target.value;
    setStartDate(val);
    setErrorMsg('');
    if (endDate && endDate < val) {
      setEndDate(val);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!itemName.trim()) {
      setErrorMsg('Please enter what you are looking for.');
      return;
    }
    if (!startDate || !endDate) {
      setErrorMsg('Please select a start and end date.');
      return;
    }
    if (endDate < startDate) {
      setErrorMsg('End date must be on or after the start date.');
      return;
    }

    try {
      setSubmitting(true);
      setErrorMsg('');

      await addRequest({
        item: itemName.trim(),
        category,
        start: startDate,
        end: endDate,
        degree: trustRadius,
        note: note.trim(),
      });

      navigate('/listings');
    } catch (err) {
      setErrorMsg(err.message || 'Failed to submit request to server.');
    } finally {
      setSubmitting(false);
    }
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
            <div
              style={{
                backgroundColor: '#fef2f2',
                border: '1px solid #f87171',
                color: '#b91c1c',
                padding: '12px 16px',
                borderRadius: '8px',
                fontSize: '14px',
                marginBottom: '16px',
                lineHeight: 1.4,
              }}
            >
              <strong>Validation Notice:</strong> {errorMsg}
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
                <label htmlFor="category">Category</label>
                <select
                  id="category"
                  value={category}
                  onChange={(e) => setCategory(e.target.value)}
                >
                  <option value="Academics">Academics</option>
                  <option value="Electronics">Electronics</option>
                  <option value="Lab Gear">Lab Gear</option>
                  <option value="Photography">Photography</option>
                  <option value="Other">Other</option>
                </select>
              </div>

              <div className="form-group">
                <label htmlFor="trust-radius">Who should see this?</label>
                <select
                  id="trust-radius"
                  value={trustRadius}
                  onChange={(e) => setTrustRadius(e.target.value)}
                >
                  <option value="1">My friends and classmates (Degree 1)</option>
                  <option value="2">Friends of friends (Degree 2)</option>
                  <option value="3">Anyone on campus (Degree 3)</option>
                </select>
              </div>
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
              <label htmlFor="note">Optional note for lenders</label>
              <input
                type="text"
                id="note"
                placeholder="e.g. Needed for Calculus III midterm, will return sanitized"
                value={note}
                onChange={(e) => setNote(e.target.value)}
              />
            </div>

            <button type="submit" className="primary-btn" disabled={submitting}>
              {submitting ? 'Submitting to campus network...' : 'Share request →'}
            </button>
            <p className="form-note">
              Verified with LoopIn Trust &amp; Safety Service. Maximum loan duration: 30 days.
            </p>
          </form>
        </section>
      </main>
    </>
  );
}
