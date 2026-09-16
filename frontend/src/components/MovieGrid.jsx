import React, { useState } from 'react';
import { Star, Clock, Ticket, Film } from 'lucide-react';

const GENRES = ['All', 'Sci-Fi', 'Action', 'Drama', 'IMAX 3D', 'Animation'];

export default function MovieGrid({ movies, onSelectMovie }) {
  const [selectedGenre, setSelectedGenre] = useState('All');

  const filteredMovies = movies.filter(m => 
    selectedGenre === 'All' ? true : m.genre?.toLowerCase().includes(selectedGenre.toLowerCase())
  );

  return (
    <section>
      <div className="section-header">
        <h2 style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
          <Film style={{ color: 'var(--primary)' }} /> Explore Movies & Shows
        </h2>
        <div className="filter-chips">
          {GENRES.map(genre => (
            <button
              key={genre}
              className={`chip ${selectedGenre === genre ? 'active' : ''}`}
              onClick={() => setSelectedGenre(genre)}
            >
              {genre}
            </button>
          ))}
        </div>
      </div>

      <div className="movie-grid">
        {filteredMovies.map(movie => (
          <div key={movie.id} className="movie-card" onClick={() => onSelectMovie(movie)}>
            <div className="movie-poster">
              <img 
                src={movie.posterUrl || 'https://images.unsplash.com/photo-1536440136628-849c177e76a1?auto=format&fit=crop&w=800&q=80'} 
                alt={movie.title} 
              />
              <div className="movie-rating">
                <Star size={14} fill="currentColor" /> {movie.rating || '8.8'}
              </div>
            </div>
            <div className="movie-details">
              <h3 className="movie-title">{movie.title}</h3>
              <div className="movie-meta">
                <span>{movie.language || 'English'}</span> • 
                <span style={{ display: 'flex', alignItems: 'center', gap: '0.2rem' }}>
                  <Clock size={13} /> {movie.durationMinutes || 148} mins
                </span>
              </div>
              <span className="movie-genre-badge">{movie.genre || 'Action / Sci-Fi'}</span>
              
              <button className="btn btn-outline btn-sm" style={{ marginTop: 'auto', width: '100%' }}>
                <Ticket size={14} /> Select Showtime
              </button>
            </div>
          </div>
        ))}
      </div>
    </section>
  );
}
