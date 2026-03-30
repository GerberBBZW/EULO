import React from 'react';
import {
  LayoutDashboard,
  Search,
  BookOpen,
  Calendar,
  User,
  LogOut,
  GraduationCap } from
'lucide-react';
import { useAuth } from '../../hooks/useAuth';
import { Page } from '../../types';
import { Avatar } from '../ui/Avatar';
interface SidebarProps {
  currentPage: Page;
  onNavigate: (page: Page) => void;
  isOpen: boolean;
  onClose: () => void;
}
export function Sidebar({
  currentPage,
  onNavigate,
  isOpen,
  onClose
}: SidebarProps) {
  const { currentUser, logout } = useAuth();
  const navItems = [
  {
    id: 'dashboard' as Page,
    label: 'Dashboard',
    icon: LayoutDashboard
  },
  {
    id: 'find-tutoring' as Page,
    label: 'Find Tutoring',
    icon: Search
  },
  {
    id: 'offer-tutoring' as Page,
    label: 'Offer Tutoring',
    icon: BookOpen
  },
  {
    id: 'my-sessions' as Page,
    label: 'My Sessions',
    icon: Calendar
  },
  {
    id: 'profile' as Page,
    label: 'Profile',
    icon: User
  }];

  return (
    <>
      {/* Mobile overlay */}
      <div
        className={`fixed inset-0 z-20 bg-slate-900/50 transition-opacity lg:hidden ${isOpen ? 'opacity-100' : 'opacity-0 pointer-events-none'}`}
        onClick={onClose} />


      {/* Sidebar */}
      <aside
        className={`
          fixed inset-y-0 left-0 z-30 w-64 bg-slate-900 text-white transform transition-transform duration-300 ease-in-out lg:translate-x-0 lg:static lg:inset-0
          ${isOpen ? 'translate-x-0' : '-translate-x-full'}
        `}>

        <div className="flex flex-col h-full">
          {/* Logo */}
          <div className="flex items-center p-6 border-b border-slate-800">
            <div className="flex items-center justify-center w-10 h-10 rounded-lg bg-teal-500 text-white mr-3">
              <GraduationCap className="w-6 h-6" />
            </div>
            <div>
              <h1 className="text-xl font-bold tracking-tight">EULO</h1>
              <p className="text-xs text-slate-400">
                Students helping students
              </p>
            </div>
          </div>

          {/* Navigation */}
          <nav className="flex-1 px-4 py-6 space-y-1 overflow-y-auto">
            {navItems.map((item) => {
              const isActive = currentPage === item.id;
              return (
                <button
                  key={item.id}
                  onClick={() => {
                    onNavigate(item.id);
                    onClose();
                  }}
                  className={`
                    flex items-center w-full px-3 py-3 rounded-lg text-sm font-medium transition-colors
                    ${isActive ? 'bg-teal-600 text-white' : 'text-slate-300 hover:bg-slate-800 hover:text-white'}
                  `}>

                  <item.icon
                    className={`w-5 h-5 mr-3 ${isActive ? 'text-white' : 'text-slate-400'}`} />

                  {item.label}
                </button>);

            })}
          </nav>

          {/* User Profile */}
          <div className="p-4 border-t border-slate-800">
            <div className="flex items-center mb-4 px-2">
              <Avatar
                name={currentUser?.name || 'User'}
                src={currentUser?.avatarUrl}
                size="sm"
                className="mr-3 ring-2 ring-slate-700" />

              <div className="overflow-hidden">
                <p className="text-sm font-medium text-white truncate">
                  {currentUser?.name}
                </p>
                <p className="text-xs text-slate-400 truncate">
                  {currentUser?.vocationalGroup}
                </p>
              </div>
            </div>
            <button
              onClick={logout}
              className="flex items-center w-full px-3 py-2 text-sm font-medium text-slate-400 rounded-lg hover:bg-slate-800 hover:text-white transition-colors">

              <LogOut className="w-4 h-4 mr-3" />
              Sign Out
            </button>
          </div>
        </div>
      </aside>
    </>);

}