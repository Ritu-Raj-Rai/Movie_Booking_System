import React from 'react';
import { X, CheckCircle, Ticket, Calendar, Clock, MapPin, Download } from 'lucide-react';

export default function TicketModal({ booking, movie, onClose }) {
  if (!booking) return null;

  return (
    <div className="modal-overlay active">
      <div className="modal-content" style={{ maxWidth: '520px', padding: '0', background: 'transparent' }}>
        <div className="ticket-pass">
          <div className="ticket-pass-header">
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.6rem' }}>
              <CheckCircle size={22} color="#10b981" />
              <div>
                <div style={{ fontWeight: '700', fontSize: '1.1rem' }}>Booking Confirmed</div>
                <div style={{ fontSize: '0.75rem', opacity: 0.9 }}>ID: #{booking.id || 'BK-98421'}</div>
              </div>
            </div>
            <button className="close-btn" style={{ position: 'static', background: 'rgba(255,255,255,0.2)' }} onClick={onClose}>
              <X size={18} />
            </button>
          </div>

          <div className="ticket-pass-body">
            <div>
              <h3 style={{ fontSize: '1.3rem', color: '#fff', marginBottom: '0.5rem' }}>{movie?.title || 'Inception: Resurgence'}</h3>
              
              <div style={{ fontSize: '0.88rem', color: 'var(--text-muted)', display: 'flex', flexDirection: 'column', gap: '0.4rem', marginBottom: '1rem' }}>
                <span style={{ display: 'flex', alignItems: 'center', gap: '0.4rem' }}>
                  <MapPin size={14} color="var(--accent-cyan)" /> PVR Directors Cut • Screen 1
                </span>
                <span style={{ display: 'flex', alignItems: 'center', gap: '0.4rem' }}>
                  <Calendar size={14} color="var(--primary)" /> Today, 7:30 PM
                </span>
                <span style={{ display: 'flex', alignItems: 'center', gap: '0.4rem' }}>
                  <Ticket size={14} color="var(--accent-gold)" /> Seats: <strong style={{ color: '#fff' }}>{booking.seatNames || 'F5, F6'}</strong>
                </span>
              </div>

              <div style={{ fontSize: '1.1rem', fontWeight: '700', color: 'var(--accent-cyan)' }}>
                Paid: ${(booking.totalAmount || 26.40).toFixed(2)}
              </div>
            </div>

            {/* QR Code SVG Graphic */}
            <div style={{ background: '#fff', padding: '0.75rem', borderRadius: '14px', textAlign: 'center', display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center' }}>
              <svg viewBox="0 0 100 100" style={{ width: '100%', height: 'auto' }}>
                <path d="M0,0 h30 v30 h-30 z M10,10 h10 v10 h-10 z" fill="#0f172a" />
                <path d="M70,0 h30 v30 h-30 z M80,10 h10 v10 h-10 z" fill="#0f172a" />
                <path d="M0,70 h30 v30 h-30 z M10,80 h10 v10 h-10 z" fill="#0f172a" />
                <path d="M40,10 h20 v10 h-20 z M10,40 h10 v20 h-10 z M40,40 h30 v10 h-30 z M30,60 h10 v30 h-10 z M60,70 h20 v20 h-20 z" fill="#0f172a" />
              </svg>
              <span style={{ fontSize: '0.65rem', color: '#64748b', fontWeight: '700', marginTop: '0.2rem' }}>SCAN AT GATE</span>
            </div>
          </div>

          <div style={{ padding: '1rem 1.5rem', background: 'rgba(255,255,255,0.03)', borderTop: '1px border-color', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <span style={{ fontSize: '0.8rem', color: 'var(--text-subtle)' }}>Show this QR code at cinema entry</span>
            <button className="btn btn-primary btn-sm" onClick={() => window.print()}>
              <Download size={14} /> Download Ticket
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
