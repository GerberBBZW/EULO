import React, { useState, createContext, useContext, ReactNode, useEffect } from 'react';
import { User } from '../types';
import { login as apiLogin, getToken, setToken, clearToken, getUser } from '../api';

interface AuthContextType {
  currentUser: User | null;
  isAuthenticated: boolean;
  login: (email: string, password: string) => Promise<void>;
  logout: () => void;
  refreshUser: () => Promise<void>;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [currentUser, setCurrentUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);

  const loadUserFromToken = async () => {
    const token = getToken();
    if (!token) return;
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const user = await getUser(payload.sub);
      setCurrentUser(user);
    } catch {
      clearToken();
      setCurrentUser(null);
    }
  };

  useEffect(() => {
    loadUserFromToken().finally(() => setLoading(false));
  }, []);

  const login = async (email: string, password: string) => {
    const { token, user } = await apiLogin(email, password);
    setToken(token);
    setCurrentUser(user);
  };

  const logout = () => {
    clearToken();
    setCurrentUser(null);
  };

  const refreshUser = async () => {
    if (!currentUser) return;
    try {
      const user = await getUser(currentUser.id);
      setCurrentUser(user);
    } catch {
      logout();
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-slate-50 flex items-center justify-center">
        <div className="text-slate-500">Loading...</div>
      </div>
    );
  }

  return (
    <AuthContext.Provider value={{ currentUser, isAuthenticated: !!currentUser, login, logout, refreshUser }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}
