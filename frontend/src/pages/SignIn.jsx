import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useApp } from '../context/AppContext';

export default function SignIn() {
  const navigate = useNavigate();
  const { signIn } = useApp();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  const handleSubmit = (e) => {
    e.preventDefault();
    if (email) {
      signIn(email, password);
      navigate('/');
    }
  };

  return (
    <div className="auth-page">
      <main className="auth-main">
        <section className="auth-card">
          <p className="section-kicker">Welcome back</p>
          <h1>Sign in to LoopIn.</h1>
          <p className="auth-intro">Use your college email to pick up where you left off.</p>
          <form className="auth-form" onSubmit={handleSubmit}>
            <div className="form-group">
              <label htmlFor="signin-email">College email</label>
              <input
                id="signin-email"
                type="email"
                placeholder="you@university.edu"
                autoComplete="email"
                required
                value={email}
                onChange={(e) => setEmail(e.target.value)}
              />
            </div>
            <div className="form-group">
              <label htmlFor="signin-password">Password</label>
              <input
                id="signin-password"
                type="password"
                autoComplete="current-password"
                required
                value={password}
                onChange={(e) => setPassword(e.target.value)}
              />
            </div>
            <button className="primary-btn" type="submit">
              Sign in <span aria-hidden="true">→</span>
            </button>
          </form>
          <p className="auth-switch">
            New here? <Link to="/signup">Create an account</Link>
          </p>
        </section>
      </main>
    </div>
  );
}
