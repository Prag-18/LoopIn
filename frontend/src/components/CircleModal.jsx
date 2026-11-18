import React from 'react';
import { useApp } from '../context/AppContext';

export default function CircleModal({ isOpen, onClose }) {
  const { friends, removeFriend } = useApp();

  if (!isOpen) return null;

  return (
    <div className="modal-backdrop" onClick={onClose} role="dialog" aria-modal="true">
      <div className="circle-modal" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <div>
            <h3 className="modal-title">My Campus Circle</h3>
            <p className="modal-subtitle">
              {friends.length} direct connection{friends.length === 1 ? '' : 's'} (1st Degree)
            </p>
          </div>
          <button className="modal-close-btn" onClick={onClose} aria-label="Close modal">
            ✕
          </button>
        </div>

        <div className="circle-explainer">
          <span className="explainer-icon">ℹ️</span>
          <span>
            Gear hosted by students in your Circle automatically appears as <strong>1st Degree (Friends)</strong> in your feed!
          </span>
        </div>

        <div className="friends-list">
          {friends.length === 0 ? (
            <div className="empty-friends">
              <p>No friends added yet.</p>
              <small>Browse items in "Available Now" and click <strong>+ Add Friend</strong> on any item host to expand your campus circle!</small>
            </div>
          ) : (
            friends.map((friend) => (
              <div key={friend.email} className="friend-row">
                <div className="friend-avatar">
                  {friend.name ? friend.name.charAt(0).toUpperCase() : '👤'}
                </div>
                <div className="friend-details">
                  <span className="friend-name">{friend.name}</span>
                  <span className="friend-email">{friend.email}</span>
                </div>
                <button
                  type="button"
                  className="unfriend-btn"
                  onClick={() => removeFriend(friend.email)}
                  title="Remove from your Circle"
                >
                  Remove
                </button>
              </div>
            ))
          )}
        </div>

        <div className="modal-footer">
          <button type="button" className="primary-btn" onClick={onClose}>
            Done
          </button>
        </div>
      </div>
    </div>
  );
}
