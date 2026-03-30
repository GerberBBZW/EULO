import React, { useState, createContext, useContext } from 'react';
import { User } from '../types';
interface AuthContextType {
  currentUser: User | null;
  isAuthenticated: boolean;
  login: (email: string) => void;
  logout: () => void;
}
const AuthContext = createContext<AuthContextType | undefined>(undefined);
// Mock user data
const MOCK_USER: User = {
  id: 'u1',
  name: 'Alex Rivera',
  email: 'alex.rivera@school.edu',
  role: 'student',
  vocationalGroup: 'Information Technology',
  avatarUrl:
  'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?ixlib=rb-1.2.1&auto=format&fit=crop&w=800&q=80',
  bio: 'IT student passionate about coding and math. Happy to help with programming basics!',
  subjectsTutored: ['Web Development', 'Mathematics'],
  sessionsCompleted: 12
};
export function AuthProvider({ children }: {children: ReactNode;}) {
  const [currentUser, setCurrentUser] = useState<User | null>(null);
  const login = (email: string) => {
    // Simulate login - in a real app this would validate credentials
    setCurrentUser(MOCK_USER);
  };
  const logout = () => {
    setCurrentUser(null);
  };
  return (
    <AuthContext.Provider
      value={{
        currentUser,
        isAuthenticated: !!currentUser,
        login,
        logout
      }}>

      {children}
    </AuthContext.Provider>);

}
export function useAuth() {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}