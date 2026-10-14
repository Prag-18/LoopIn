import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useApp } from '../context/AppContext';

export default function SignUp() {
  const navigate = useNavigate();
  const { signUp } = useApp();
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  const handleSubmit = (e) => {
    e.preventDefault();
    if (name && email) {
      signUp(name, email, password);
      navigate('/');
    }
  };

  return (
    <div className="auth-page">
      <main className="auth-main">
        <section className="auth-card">
          <p className="section-kicker">Join your campus</p>
          <h1>Make borrowing easier.</h1>
          <p className="auth-intro">
            Create an account with your college email. It helps keep LoopIn local and trusted.
          </p>
          <form className="auth-form" onSubmit={handleSubmit}>
            <div className="form-group">
              <label htmlFor="signup-name">Your name</label>
              <input
                id="signup-name"
                type="text"
                placeholder="Pragati"
                autoComplete="name"
                required
                value={name}
                onChange={(e) => setName(e.target.value)}
              />
            </div>
            <div className="form-group">
              <label htmlFor="signup-email">College email</label>
              <input
                id="signup-email"
                type="email"
                placeholder="pragati@university.edu"
                autoComplete="email"
                required
                value={email}
                onChange={(e) => setEmail(e.target.value)}
              />
            </div>
            <div className="form-group">
              <label htmlFor="signup-password">Create a password</label>
              <input
                id="signup-password"
                type="password"
                minLength={6}
                autoComplete="new-password"
                required
                value={password}
                onChange={(e) => setPassword(e.target.value)}
              />
            </div>
            <button className="primary-btn" type="submit">
              Create account <span aria-hidden="true">→</span>
            </button>
          </form>
          <p className="auth-switch">
            Already have an account? <Link to="/signin">Sign in</Link>
          </p>
        </section>
      </main>
    </div>
  );
}
