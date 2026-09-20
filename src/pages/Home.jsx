import React from 'react';
import { Link } from 'react-router-dom';
import Hero from '../components/Hero';

export default function Home() {
  return (
    <>
      <Hero
        eyebrow="A better way to borrow nearby"
        title="The thing you need is probably already on campus."
        subtitle="Borrow from people you know — classmates, neighbours, and friends of friends."
        activeMode="request"
      />
      <main className="main-container single-column">
        <section className="panel-card text-center">
          <p className="section-kicker">Start here</p>
          <h2>What are you looking for?</h2>
          <p className="panel-desc">
            Post a request and we’ll put it in front of the people most likely to help — no campus-wide spam.
          </p>
          <Link className="primary-btn button-link" to="/request">
            Post a request <span aria-hidden="true">→</span>
          </Link>
        </section>
      </main>
    </>
  );
}
