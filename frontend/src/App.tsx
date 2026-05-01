import React, { useState } from 'react';
import { AuthProvider, useAuth } from './hooks/useAuth';
import { LoginPage } from './pages/LoginPage';
import { SignUpPage } from './pages/SignUpPage';
import { DashboardPage } from './pages/DashboardPage';
import { FindTutoringPage } from './pages/FindTutoringPage';
import { OfferTutoringPage } from './pages/OfferTutoringPage';
import { MySessionsPage } from './pages/MySessionsPage';
import { ProfilePage } from './pages/ProfilePage';
import { Page } from './types';

function AppContent() {
  const { isAuthenticated } = useAuth();
  const [currentPage, setCurrentPage] = useState<Page>('dashboard');
  const [showSignUp, setShowSignUp] = useState(false);

  if (!isAuthenticated) {
    if (showSignUp) {
      return <SignUpPage onSwitchToLogin={() => setShowSignUp(false)} />;
    }
    return <LoginPage onSwitchToSignUp={() => setShowSignUp(true)} />;
  }

  switch (currentPage) {
    case 'dashboard':
      return <DashboardPage onNavigate={setCurrentPage} />;
    case 'find-tutoring':
      return <FindTutoringPage onNavigate={setCurrentPage} />;
    case 'offer-tutoring':
      return <OfferTutoringPage onNavigate={setCurrentPage} />;
    case 'my-sessions':
      return <MySessionsPage onNavigate={setCurrentPage} />;
    case 'profile':
      return <ProfilePage onNavigate={setCurrentPage} />;
    default:
      return <DashboardPage onNavigate={setCurrentPage} />;
  }
}

export function App() {
  return (
    <AuthProvider>
      <AppContent />
    </AuthProvider>
  );
}
