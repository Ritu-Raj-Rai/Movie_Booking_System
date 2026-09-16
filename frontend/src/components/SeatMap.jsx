import React, { useState, useEffect } from 'react';
import { X, Clock, ShieldCheck, Tag, CreditCard, Sparkles } from 'lucide-react';

const ROWS = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H'];
const COLS = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12];

export default function SeatMap({ movie, show, heldSeatIds = [], bookedSeatIds = [], onClose, onConfirmHoldAndPay }) {
  const [selectedSeats, setSelectedSeats] = useState([]);
  const [promoCode, setPromoCode] = useState('');
  const [appliedDiscount, setAppliedDiscount] = useState(0);
  const [holdTimer, setHoldTimer] = useState(600); // 10 minutes in seconds
  const [isHolding, setIsHolding] = useState(false);

  // Timer Countdown Effect
  useEffect(() => {
    let interval = null;
    if (isHolding && holdTimer > 0) {
      interval = setInterval(() => setHoldTimer(t => t - 1), 1000);
    } else if (holdTimer === 0) {
      setIsHolding(false);
      alert('Seat hold time expired. Please re-select your seats.');
      setSelectedSeats([]);
    }
    return () => clearInterval(interval);
  }, [isHolding, holdTimer]);

  const toggleSeat = (seatId, isPremium, isBooked, isHeld) => {
    if (isBooked || isHeld) return;
    if (selectedSeats.some(s => s.id === seatId)) {
      setSelectedSeats(selectedSeats.filter(s => s.id !== seatId));
    } else {
      setSelectedSeats([...selectedSeats, { id: seatId, isPremium }]);
    }
  };

  const handleApplyPromo = () => {
    if (promoCode.trim().toUpperCase() === 'SAVE20') {
      setAppliedDiscount(20);
    } else if (promoCode.trim().toUpperCase() === 'CINEMA50') {
      setAppliedDiscount(50);
    } else {
      alert('Invalid promo code! Try SAVE20 or CINEMA50');
      setAppliedDiscount(0);
    }
  };

  // Pricing math
  const baseRate = show?.price || 12.00;
  const rawSubtotal = selectedSeats.reduce((sum, seat) => {
    return sum + (seat.isPremium ? baseRate * 1.5 : baseRate);
  }, 0);
  
  const surgeMultiplier = rawSubtotal > 0 ? 0.10 : 0; // 10% surge pricing add-on
  const surgeAmount = rawSubtotal * surgeMultiplier;
  const discountAmount = (rawSubtotal * appliedDiscount) / 100;
  const totalAmount = Math.max(0, rawSubtotal + surgeAmount - discountAmount);

  const formatTime = (seconds) => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
  };

  const handleHoldCheckout = () => {
    if (selectedSeats.length === 0) return;
    setIsHolding(true);
    onConfirmHoldAndPay({
      seatIds: selectedSeats.map(s => s.id),
      totalAmount,
      promoCode: appliedDiscount > 0 ? promoCode : null
    });
  };

  return (
    <div className="modal-overlay active">
      <div className="modal-content" style={{ maxWidth: '1000px' }}>
        <button className="close-btn" onClick={onClose}><X size={20} /></button>

        <div style={{ marginBottom: '1.5rem' }}>
          <h2 style={{ fontSize: '1.8rem', color: '#fff' }}>{movie?.title}</h2>
          <div style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>
            Screen 1 (IMAX 4K) • {show?.showTime || '7:30 PM Today'} • Base Price: ${baseRate.toFixed(2)}
          </div>
        </div>

        <div className="seat-booking-container">
          {/* Main Seat Grid Layout */}
          <div>
            <div className="screen-visualizer">
              <div className="screen-arc"></div>
              <div className="screen-label">IMAX CINEMA SCREEN</div>
            </div>

            <div className="seat-matrix">
              {ROWS.map(row => (
                <div key={row} className="seat-row">
                  <span className="row-letter">{row}</span>
                  {COLS.map(col => {
                    const seatId = (row.charCodeAt(0) - 65) * 12 + col;
                    const isPremium = row === 'G' || row === 'H';
                    const isSelected = selectedSeats.some(s => s.id === seatId);
                    const isHeld = heldSeatIds.includes(seatId);
                    const isBooked = bookedSeatIds.includes(seatId);

                    let seatClass = 'seat-item';
                    if (isPremium) seatClass += ' premium';
                    if (isSelected) seatClass += ' selected';
                    if (isHeld) seatClass += ' held';
                    if (isBooked) seatClass += ' booked';

                    return (
                      <div
                        key={col}
                        className={seatClass}
                        onClick={() => toggleSeat(seatId, isPremium, isBooked, isHeld)}
                        title={`Seat ${row}${col} ${isPremium ? '(Premium VIP)' : '(Regular)'}`}
                      >
                        {col}
                      </div>
                    );
                  })}
                </div>
              ))}
            </div>

            <div className="seat-legend-bar">
              <div className="legend-box-item">
                <div className="legend-swatch" style={{ background: 'var(--seat-available)' }}></div>
                <span>Available ($12)</span>
              </div>
              <div className="legend-box-item">
                <div className="legend-swatch" style={{ background: 'var(--seat-premium)', border: '1px solid var(--seat-premium)' }}></div>
                <span>Premium VIP ($18)</span>
              </div>
              <div className="legend-box-item">
                <div className="legend-swatch" style={{ background: 'var(--primary)' }}></div>
                <span>Selected</span>
              </div>
              <div className="legend-box-item">
                <div className="legend-swatch" style={{ background: 'var(--seat-held)' }}></div>
                <span>Held</span>
              </div>
              <div className="legend-box-item">
                <div className="legend-swatch" style={{ background: 'var(--seat-booked)' }}></div>
                <span>Booked</span>
              </div>
            </div>
          </div>

          {/* Checkout & Fare Breakdown Sidebar */}
          <div className="checkout-summary">
            {isHolding && (
              <div className="hold-timer-alert">
                <div style={{ display: 'flex', alignItems: 'center', gap: '0.4rem' }}>
                  <Clock size={16} /> Seats Locked
                </div>
                <div className="hold-timer-clock">{formatTime(holdTimer)}</div>
              </div>
            )}

            <h3 style={{ marginBottom: '1rem', fontSize: '1.2rem', color: '#fff' }}>Booking Summary</h3>
            
            <div className="summary-row">
              <span>Selected Seats ({selectedSeats.length})</span>
              <span style={{ fontWeight: '600', color: '#fff' }}>
                {selectedSeats.length > 0 
                  ? selectedSeats.map(s => `${ROWS[Math.floor((s.id - 1) / 12)]}${(s.id - 1) % 12 + 1}`).join(', ')
                  : 'None'}
              </span>
            </div>

            <div className="summary-row">
              <span>Subtotal</span>
              <span>${rawSubtotal.toFixed(2)}</span>
            </div>

            <div className="summary-row">
              <span>Dynamic Surge (10%)</span>
              <span>+${surgeAmount.toFixed(2)}</span>
            </div>

            {appliedDiscount > 0 && (
              <div className="summary-row" style={{ color: 'var(--accent-green)' }}>
                <span>Promo Discount ({appliedDiscount}%)</span>
                <span>-${discountAmount.toFixed(2)}</span>
              </div>
            )}

            <div className="summary-row total">
              <span>Total Amount</span>
              <span style={{ color: 'var(--accent-cyan)' }}>${totalAmount.toFixed(2)}</span>
            </div>

            {/* Promo Code Input */}
            <div style={{ marginTop: '1.25rem' }}>
              <label style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Promo Code (e.g. SAVE20)</label>
              <div style={{ display: 'flex', gap: '0.5rem', marginTop: '0.3rem' }}>
                <input 
                  type="text" 
                  className="form-input" 
                  placeholder="Enter code" 
                  value={promoCode}
                  onChange={(e) => setPromoCode(e.target.value)}
                />
                <button className="btn btn-secondary btn-sm" onClick={handleApplyPromo}>
                  <Tag size={14} /> Apply
                </button>
              </div>
            </div>

            <button 
              className="btn btn-primary" 
              style={{ marginTop: '1.5rem', width: '100%', padding: '0.85rem' }}
              disabled={selectedSeats.length === 0}
              onClick={handleHoldCheckout}
            >
              <CreditCard size={18} /> Confirm & Pay (${totalAmount.toFixed(2)})
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
