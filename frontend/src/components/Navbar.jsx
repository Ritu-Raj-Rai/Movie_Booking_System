import React from 'react';
import { Film, User, LogOut, Ticket, Sparkles } from 'lucide-react';

export default function Navbar({ user, onOpenAuth, onLogout, onOpenMyBookings }) {
  return (
    <header className="app-header">
      <div className="logo-brand" onClick={() => window.location.reload()}>
        <div className="logo-icon">
          <Film size={24} />
        </div>
        <div>Cine<span>Pass</span></div>
      </div>

      <div className="nav-actions">
        {user ? (
          <>
            <button className="btn btn-outline btn-sm" onClick={onOpenMyBookings}>
              <Ticket size={16} /> My Bookings
            </button>
            <div className="user-badge">
              <User size={14} />
              <span>{user.email}</span>
              <span className="role">{user.role || 'USER'}</span>
            </div>
            <button className="btn btn-secondary btn-sm" onClick={onLogout} title="Logout">
              <LogOut size={16} />
            </button>
          </>
        ) : (
          <button className="btn btn-primary" onClick={onOpenAuth}>
            <Sparkles size={16} /> Sign In / Register
          </button>
        )}
      </div>
    </header>
  );
}
