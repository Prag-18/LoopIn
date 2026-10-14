import React from 'react';
import { Routes, Route } from 'react-router-dom';
import Navbar from './components/Navbar';
import Footer from './components/Footer';
import Home from './pages/Home';
import Listings from './pages/Listings';
import Request from './pages/Request';
import SignIn from './pages/SignIn';
import SignUp from './pages/SignUp';

export default function App() {
  return (
    <div className="app-wrapper">
      <Navbar />
      <div className="content-body">
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/listings" element={<Listings />} />
          <Route path="/request" element={<Request />} />
          <Route path="/signin" element={<SignIn />} />
          <Route path="/signup" element={<SignUp />} />
        </Routes>
      </div>
      <Footer />
    </div>
  );
}
