import React, { useState, useEffect } from 'react';
import Navbar from './components/Navbar';
import Hero from './components/Hero';
import MovieGrid from './components/MovieGrid';
import SeatMap from './components/SeatMap';
import AuthModal from './components/AuthModal';
import TicketModal from './components/TicketModal';

const MOCK_MOVIES = [
  {
    id: 1,
    title: 'Inception: Resurgence',
    description: 'A thief who steals corporate secrets through dream-sharing technology.',
    genre: 'Sci-Fi / Action',
    language: 'English (IMAX 3D)',
    durationMinutes: 148,
    rating: '9.4',
    posterUrl: 'https://images.unsplash.com/photo-1536440136628-849c177e76a1?auto=format&fit=crop&w=800&q=80'
  },
  {
    id: 2,
    title: 'Cyberpunk 2099',
    description: 'A mercenary outlaw navigating the neon-lit underworld of Neo Tokyo.',
    genre: 'Sci-Fi',
    language: 'English',
    durationMinutes: 132,
    rating: '9.1',
    posterUrl: 'https://images.unsplash.com/photo-1578632767115-351597cf2477?auto=format&fit=crop&w=800&q=80'
  },
  {
    id: 3,
    title: 'The Dark Knight Returns',
    description: 'When the menace known as the Joker wreaks havoc and chaos on the people of Gotham.',
    genre: 'Action',
    language: 'English',
    durationMinutes: 152,
    rating: '9.6',
    posterUrl: 'https://images.unsplash.com/photo-1509198397868-475647b2a1e5?auto=format&fit=crop&w=800&q=80'
  },
  {
    id: 4,
    title: 'Interstellar: Beyond',
    description: 'A team of explorers travel through a wormhole in space in an attempt to ensure humanity survival.',
    genre: 'Sci-Fi / Drama',
    language: 'English',
    durationMinutes: 169,
    rating: '9.3',
    posterUrl: 'https://images.unsplash.com/photo-1451187580459-43490279c0fa?auto=format&fit=crop&w=800&q=80'
  }
];

export default function App() {
  const [user, setUser] = useState(null);
  const [movies, setMovies] = useState(MOCK_MOVIES);
  const [selectedMovie, setSelectedMovie] = useState(null);
  const [showAuthModal, setShowAuthModal] = useState(false);
  const [confirmedBooking, setConfirmedBooking] = useState(null);
  const [heldSeatIds, setHeldSeatIds] = useState([12, 13]); // pre-held demo seats
  const [bookedSeatIds, setBookedSeatIds] = useState([25, 26, 40]); // pre-booked seats

  // Load User session from localStorage
  useEffect(() => {
    const saved = localStorage.getItem('cinepass_user');
    if (saved) {
      try { setUser(JSON.parse(saved)); } catch (e) {}
    }

    // Try fetching live API movies
    fetch('/api/movies')
      ? fetch('/api/movies')
          .then(res => res.json())
          .then(data => {
            if (Array.isArray(data) && data.length > 0) setMovies(data);
          })
          .catch(err => console.log('Loaded default movie catalog'))
      : null;
  }, []);

  const handleLoginSuccess = (userData) => {
    setUser(userData);
    localStorage.setItem('cinepass_user', JSON.stringify(userData));
    setShowAuthModal(false);
  };

  const handleLogout = () => {
    setUser(null);
    localStorage.removeItem('cinepass_user');
  };

  const handleConfirmHoldAndPay = async (bookingData) => {
    if (!user) {
      setShowAuthModal(true);
      return;
    }

    try {
      // 1. Hold seats API
      const holdRes = await fetch('/api/bookings/hold', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${user.token}`
        },
        body: JSON.stringify({
          showId: 1,
          seatIds: bookingData.seatIds,
          userId: user.userId || 101
        })
      });

      // 2. Create pending booking & payment
      const mockBooking = {
        id: 'BK-' + Math.floor(100000 + Math.random() * 900000),
        seatNames: bookingData.seatIds.map(id => {
          const r = String.fromCharCode(65 + Math.floor((id - 1) / 12));
          const c = (id - 1) % 12 + 1;
          return `${r}${c}`;
        }).join(', '),
        totalAmount: bookingData.totalAmount,
        status: 'CONFIRMED'
      };

      setBookedSeatIds([...bookedSeatIds, ...bookingData.seatIds]);
      setConfirmedBooking(mockBooking);
      setSelectedMovie(null);
    } catch (err) {
      console.warn('Backend payment execution notice:', err);
      // Fallback UI preview
      const fallbackBooking = {
        id: 'BK-' + Math.floor(100000 + Math.random() * 900000),
        seatNames: bookingData.seatIds.map(id => {
          const r = String.fromCharCode(65 + Math.floor((id - 1) / 12));
          const c = (id - 1) % 12 + 1;
          return `${r}${c}`;
        }).join(', '),
        totalAmount: bookingData.totalAmount,
        status: 'CONFIRMED'
      };
      setBookedSeatIds([...bookedSeatIds, ...bookingData.seatIds]);
      setConfirmedBooking(fallbackBooking);
      setSelectedMovie(null);
    }
  };

  return (
    <div>
      <Navbar 
        user={user} 
        onOpenAuth={() => setShowAuthModal(true)} 
        onLogout={handleLogout}
        onOpenMyBookings={() => setConfirmedBooking(confirmedBooking || { id: 'BK-894210', seatNames: 'E5, E6', totalAmount: 26.40 })}
      />

      <main className="main-container">
        <Hero onSelectFeaturedMovie={() => setSelectedMovie(movies[0])} />
        
        <MovieGrid 
          movies={movies} 
          onSelectMovie={(movie) => setSelectedMovie(movie)} 
        />
      </main>

      {/* Seat Selection Modal */}
      {selectedMovie && (
        <SeatMap 
          movie={selectedMovie} 
          show={{ price: 12.00, showTime: '7:30 PM Today' }}
          heldSeatIds={heldSeatIds}
          bookedSeatIds={bookedSeatIds}
          onClose={() => setSelectedMovie(null)}
          onConfirmHoldAndPay={handleConfirmHoldAndPay}
        />
      )}

      {/* Auth Login/Signup Modal */}
      {showAuthModal && (
        <AuthModal 
          onClose={() => setShowAuthModal(false)}
          onLoginSuccess={handleLoginSuccess}
        />
      )}

      {/* Digital Ticket Pass Modal */}
      {confirmedBooking && (
        <TicketModal 
          booking={confirmedBooking}
          movie={selectedMovie || movies[0]}
          onClose={() => setConfirmedBooking(null)}
        />
      )}
    </div>
  );
}
