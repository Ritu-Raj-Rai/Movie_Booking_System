import React from 'react';
import { Play, Star, Flame } from 'lucide-react';

export default function Hero({ onSelectFeaturedMovie }) {
  return (
    <div className="hero-banner">
      <div className="hero-overlay"></div>
      <div className="hero-content">
        <div className="hero-tag">
          <Flame size={14} /> NOW SHOWING IN 4K & IMAX 3D
        </div>
        <h1>Inception: Resurgence</h1>
        <p>
          Experience the mind-bending cinematic spectacle. Dynamic seating, ultra-high frame rate projection, and Dolby Atmos audio.
        </p>
        <div style={{ display: 'flex', gap: '1rem', alignItems: 'center' }}>
          <button className="btn btn-primary" onClick={onSelectFeaturedMovie}>
            <Play size={16} fill="currentColor" /> Book Tickets Now
          </button>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.4rem', color: 'var(--accent-gold)', fontWeight: '700' }}>
            <Star size={18} fill="currentColor" /> 9.4/10 IMDb
          </div>
        </div>
      </div>
    </div>
  );
}
