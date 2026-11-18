import React, { useState } from 'react';
import { useApp } from '../context/AppContext';

export default function HostItemModal({ isOpen, onClose }) {
  const { addListing, profile } = useApp();

  const [title, setTitle] = useState('');
  const [category, setCategory] = useState('Electronics');
  const [condition, setCondition] = useState('EXCELLENT');
  const [degree, setDegree] = useState('2');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [errorMsg, setErrorMsg] = useState('');

  if (!isOpen) return null;

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!title.trim()) {
      setErrorMsg('Please enter an item name.');
      return;
    }

    try {
      setIsSubmitting(true);
      setErrorMsg('');

      await addListing({
        title: title.trim(),
        category,
        condition,
        degree: Number(degree),
      });

      setTitle('');
      onClose();
    } catch (err) {
      setErrorMsg(err.message || 'Failed to host item on campus network.');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="modal-backdrop" onClick={onClose} role="dialog" aria-modal="true">
      <div className="circle-modal" onClick={(e) => e.stopPropagation()} style={{ maxWidth: '480px' }}>
        <div className="modal-header">
          <div>
            <h3 className="modal-title">Host Gear for Campus</h3>
            <p className="modal-subtitle">
              Lend your items to friends, mutuals, or verified campus students.
            </p>
          </div>
          <button className="modal-close-btn" onClick={onClose} aria-label="Close modal">
            ✕
          </button>
        </div>

        {errorMsg && (
          <div style={{ background: '#fef2f2', color: '#b91c1c', padding: '10px 14px', borderRadius: '8px', fontSize: '13px', marginBottom: '12px' }}>
            {errorMsg}
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div className="form-group" style={{ marginBottom: '14px' }}>
            <label htmlFor="host-item-title" style={{ display: 'block', fontSize: '13px', fontWeight: '600', marginBottom: '6px' }}>
              Item Name & Model
            </label>
            <input
              id="host-item-title"
              type="text"
              placeholder="e.g. Sony Wireless Headphones, TI-84 Plus, GoPro..."
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              style={{ width: '100%', padding: '10px 14px', borderRadius: '8px', border: '1px solid var(--line)', fontSize: '14px' }}
              autoFocus
            />
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px', marginBottom: '14px' }}>
            <div className="form-group">
              <label htmlFor="host-item-category" style={{ display: 'block', fontSize: '13px', fontWeight: '600', marginBottom: '6px' }}>
                Category
              </label>
              <select
                id="host-item-category"
                value={category}
                onChange={(e) => setCategory(e.target.value)}
                style={{ width: '100%', padding: '9px 12px', borderRadius: '8px', border: '1px solid var(--line)', fontSize: '13px' }}
              >
                <option value="Electronics">Electronics</option>
                <option value="Academics">Academics</option>
                <option value="Creative & Media">Creative & Media</option>
                <option value="Home & Dorm">Home & Dorm</option>
                <option value="Tools">Tools</option>
                <option value="Sports & Outdoors">Sports & Outdoors</option>
              </select>
            </div>

            <div className="form-group">
              <label htmlFor="host-item-condition" style={{ display: 'block', fontSize: '13px', fontWeight: '600', marginBottom: '6px' }}>
                Condition
              </label>
              <select
                id="host-item-condition"
                value={condition}
                onChange={(e) => setCondition(e.target.value)}
                style={{ width: '100%', padding: '9px 12px', borderRadius: '8px', border: '1px solid var(--line)', fontSize: '13px' }}
              >
                <option value="EXCELLENT">Excellent (Like New)</option>
                <option value="GOOD">Good (Normal wear)</option>
                <option value="FAIR">Fair (Functional)</option>
              </select>
            </div>
          </div>

          <div className="form-group" style={{ marginBottom: '18px' }}>
            <label htmlFor="host-item-degree" style={{ display: 'block', fontSize: '13px', fontWeight: '600', marginBottom: '6px' }}>
              Who can borrow? (Trust Radius)
            </label>
            <select
              id="host-item-degree"
              value={degree}
              onChange={(e) => setDegree(e.target.value)}
              style={{ width: '100%', padding: '9px 12px', borderRadius: '8px', border: '1px solid var(--line)', fontSize: '13px' }}
            >
              <option value="1">1st Degree Only (My Direct Friends Circle)</option>
              <option value="2">Up to 2nd Degree (Friends of Friends)</option>
              <option value="3">3rd Degree (Any Verified Campus Student)</option>
            </select>
          </div>

          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '10px' }}>
            <button
              type="button"
              className="circle-done-btn"
              onClick={onClose}
              style={{ background: 'transparent', color: 'var(--muted)', border: '1px solid var(--line)' }}
            >
              Cancel
            </button>
            <button
              type="submit"
              className="primary-btn"
              disabled={isSubmitting}
              style={{ width: 'auto', padding: '9px 20px' }}
            >
              {isSubmitting ? 'Hosting...' : 'Host Item Now →'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
